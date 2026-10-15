package no.nav.tsm.oppgave

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf

fun Application.configureOppgaveModule() {
    val oppgaveService: OppgaveService by dependencies

    routing {
        entraOnBehalfOf {
            oppgaveRoutes(oppgaveService)
        }
    }
}