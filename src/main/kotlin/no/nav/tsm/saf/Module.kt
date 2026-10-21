package no.nav.tsm.saf

import io.ktor.server.application.Application

fun Application.configureSafModule() {
    configureSafDependencies()
    configrueSafRoutes()
}