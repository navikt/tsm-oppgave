package no.nav.tsm

import io.ktor.server.application.Application
import no.nav.tsm.oppgave.configureOppgaveModule
import no.nav.tsm.plugins.configureAuthentication
import no.nav.tsm.plugins.configureDatabase
import no.nav.tsm.plugins.configureDependencies
import no.nav.tsm.plugins.configureMonitoring
import no.nav.tsm.plugins.configureSerialization
import no.nav.tsm.saf.configureSafModule

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureAuthentication()
    configureDependencies()
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureSafModule()
    configureOppgaveModule()
}
