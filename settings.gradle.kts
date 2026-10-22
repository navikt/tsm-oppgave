rootProject.name = "tsm-oppgave"

val ktorVersion = "3.6.0"
val tsmKtorVersion = "1.3.6"
val exposedVersion = "1.6.0"


dependencyResolutionManagement {
    repositories {
        maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release") {
            content {
                includeGroup("no.nav.tsm")
            }
        }
        maven("https://jitpack.io")
        mavenCentral {
            content {
                excludeGroup("no.nav.tsm")
            }
        }
    }
    versionCatalogs {
        create("ktorLibs").from("io.ktor:ktor-version-catalog:${ktorVersion}")
        create("tsmKtorLibs").from("no.nav.tsm:ktor-version-catalog:${tsmKtorVersion}")
        create("exposedLibs").from("org.jetbrains.exposed:exposed-version-catalog:${exposedVersion}")
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

plugins {
    id("io.github.ben-manes.versions.settings") version "0.66.0"
}

