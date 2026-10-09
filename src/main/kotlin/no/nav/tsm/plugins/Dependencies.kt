package no.nav.tsm.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.initializeEnvironment

fun Application.configureDependencies() {
    val config = environment.config

    dependencies {
        provide<Environment> { initializeEnvironment(config) }
    }
}