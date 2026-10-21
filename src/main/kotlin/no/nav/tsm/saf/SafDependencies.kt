package no.nav.tsm.saf

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing

fun Application.configureSafModule() {
    val safService: SafService by dependencies

    routing {
        safRoutes(safService)
    }
}