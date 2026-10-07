package no.nav.tsm.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tsm.ktor.nais.NaisMonitoring

fun Application.configureMonitoring() {
    install(NaisMonitoring) {}
}


