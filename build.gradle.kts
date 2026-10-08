plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

group = "no.nav.tsm"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(25)
}
dependencies {
    // Ktor
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.metrics.micrometer)
    implementation(ktorLibs.client.callId)
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    // TSM libraries
    implementation(tsmKtorLibs.core)

    // Monitoring and Logging
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        mergeServiceFiles {}
        from("src/main/resources/logback.xml") {
            into("/")
        }
    }
}


tasks.register<JavaExec>("runLocal") {
    description = "Running the application localy"
    group = "application"
    mainClass.set("io.ktor.server.netty.EngineMain")
    classpath = sourceSets["main"].runtimeClasspath

    args("-config=application-local.conf")
    jvmArgs("-Dio.ktor.development=true", "-Dlogback.configurationFile=logback-local.xml")
}
