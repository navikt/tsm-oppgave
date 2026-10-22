package no.nav.tsm.oppgave

import io.ktor.server.application.Application

fun Application.configureOppgaveModule() {
    configureOppgaveDependencies()
    configureOppgaveApi()
}
