package no.nav.tsm.api.routes

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import no.nav.tsm.api.OppgaveService
import no.nav.tsm.ktor.auth.entra.entraMachineToken
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf

fun Application.configureOppgaveRoutes() {
    val oppgaveService: OppgaveService by dependencies

    routing {
        entraOnBehalfOf { getOppgaveRoutes(oppgaveService) }
        entraMachineToken { postOppgaveRoutes(oppgaveService) }
    }
}
