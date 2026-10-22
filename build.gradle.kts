import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import dev.detekt.gradle.Detekt
import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

group = "no.nav.tsm"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain {
        languageVersion.set(libs.versions.jvmVersion.map { JavaLanguageVersion.of(it) })
    }
}

dependencies {
    // Ktor
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.metrics.micrometer)
    implementation(ktorLibs.client.callId)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache5)
    implementation(ktorLibs.serialization.jackson3)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    // TSM libraries
    implementation(tsmKtorLibs.core)
    implementation(tsmKtorLibs.auth)

    // Database
    implementation(libs.flyway.postgres)
    implementation(libs.flyway.core)
    implementation(libs.postgresql)
    implementation(exposedLibs.core)
    implementation(exposedLibs.jdbc)
    implementation(exposedLibs.json)
    implementation(exposedLibs.kotlin.datetime)

    // Monitoring and Logging
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotest.assertions)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        mergeServiceFiles {}
        from("src/main/resources/logback.xml") {
            into("/")
        }
    }

    register<Exec>("preRunLocal") {
        description = "Running the application locally"
        group = "application"
        commandLine("./scripts/pre-dev.sh")
    }

    register<JavaExec>("runLocal") {
        description = "Running the application locally"
        group = "application"
        mainClass.set("io.ktor.server.netty.EngineMain")
        classpath = sourceSets["main"].runtimeClasspath

        args("-config=application-local.conf")
        jvmArgs(
            "-Dio.ktor.development=true",
            "-Dlogback.configurationFile=logback-local.xml",
        )

        dependsOn("preRunLocal")
    }

    configure<SpotlessExtension> {
        kotlin { ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle() }
    }

    check {
        setDependsOn(dependsOn.filter { !it.toString().contains("detekt") })
        dependsOn("spotlessApply")
    }

    withType<Detekt>().configureEach {
        config.setFrom(file("detekt.yml"))
        buildUponDefaultConfig = true

        dependsOn("spotlessApply")
    }

    named<DependencyUpdatesTask>("dependencyUpdates") {
        fun String.isNonStable(): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            val isStable = stableKeyword || regex.matches(this)
            return isStable.not()
        }

        rejectVersionIf {
            candidate.version.isNonStable()
        }
    }
}
