package no.nav.tsm

import io.ktor.server.application.Application
import no.nav.tsm.plugins.configureMonitoring

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
}
