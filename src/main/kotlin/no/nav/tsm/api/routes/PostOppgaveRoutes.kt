package no.nav.tsm.api.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.api.model.CreateOppgavePayload
import no.nav.tsm.api.model.toOppgave
import no.nav.tsm.core.model.OppgaveType

fun Route.postOppgaveRoutes(oppgaveService: OppgaveService) {
    post("/api/oppgave") {
        val oppgaveRequest: CreateOppgavePayload = call.receive()
        val oppgave = oppgaveRequest.toOppgave(OppgaveType.valueOf(oppgaveRequest.kilde))
        oppgaveService.opprettSyncOppgave(oppgave)
        call.respond(HttpStatusCode.Accepted)
    }
}
