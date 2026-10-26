package no.nav.tsm.oppgave.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import no.nav.tsm.ktor.auth.entra.entraMachineToken
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf
import no.nav.tsm.oppgave.OppgaveService

fun Application.configureOppgaveApi() {
    val oppgaveService: OppgaveService by dependencies

    routing {
        entraOnBehalfOf { getOppgaveRoutes(oppgaveService) }
        entraMachineToken { postOppgaveRoutes(oppgaveService) }
    }
}
