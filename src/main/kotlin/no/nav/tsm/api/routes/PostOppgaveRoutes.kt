package no.nav.tsm.api.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tsm.api.OppgaveService
import no.nav.tsm.api.model.OpprettOppgaveRequest
import no.nav.tsm.core.model.OppgaveKilde
import no.nav.tsm.core.model.toOppgave

fun Route.postOppgaveRoutes(oppgaveService: OppgaveService) {
    post("/api/oppgave") {
        val oppgaveRequest: OpprettOppgaveRequest = call.receive()
        val oppgave = oppgaveRequest.toOppgave(OppgaveKilde.valueOf(oppgaveRequest.kilde))
        oppgaveService.insertOppgave(oppgave)
        call.respond(HttpStatusCode.Accepted)
    }
}
