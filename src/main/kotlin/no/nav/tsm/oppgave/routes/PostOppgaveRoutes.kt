package no.nav.tsm.oppgave.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tsm.core.model.OppgaveGrunnlag
import no.nav.tsm.oppgave.OppgaveService

fun Route.postOppgaveRoutes(oppgaveService: OppgaveService) {
    post("/api/oppgave") {
        val oppgaveGrunnlag: OppgaveGrunnlag = call.receive()
        oppgaveService.insertOppgave(oppgaveGrunnlag)
        call.respond(HttpStatusCode.Accepted)
    }
}
