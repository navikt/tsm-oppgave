package no.nav.tsm.oppgave

import io.ktor.server.application.Application
import no.nav.tsm.oppgave.routes.configureOppgaveApi

fun Application.configureOppgaveModule() {
    configureOppgaveDependencies()
    configureOppgaveApi()
}
