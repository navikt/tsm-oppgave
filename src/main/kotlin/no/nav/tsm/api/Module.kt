package no.nav.tsm.api

import io.ktor.server.application.Application
import no.nav.tsm.api.routes.configureOppgaveRoutes

fun Application.configureOppgaveApi() {
    configureOppgaveRoutes()
}
