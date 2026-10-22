package no.nav.tsm.oppgave

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf
import no.nav.tsm.oppgave.routes.getOppgaveRoutes
import no.nav.tsm.oppgave.routes.postOppgaveRoutes

fun Application.configureOppgaveApi() {
    val oppgaveService: OppgaveService by dependencies

    routing {
        entraOnBehalfOf {
            route("/api") {
                getOppgaveRoutes(oppgaveService)
                postOppgaveRoutes(oppgaveService)
            }
        }
    }
}
