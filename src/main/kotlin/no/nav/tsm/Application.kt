package no.nav.tsm

import io.ktor.server.application.Application
import no.nav.tsm.oppgave.configureOppgaveDependencies
import no.nav.tsm.plugins.configureDependencies
import no.nav.tsm.plugins.configureMonitoring

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDependencies()
    configureMonitoring()
    configureOppgaveDependencies()
}
