package no.nav.tsm.api.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tsm.api.model.OpprettOppgavePayload
import no.nav.tsm.api.model.toOppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.oppgave.OppgaveService

fun Route.postOppgaveRoutes(oppgaveService: OppgaveService) {
    post("/api/oppgave") {
        val oppgaveRequest: OpprettOppgavePayload = call.receive()
        val oppgave = oppgaveRequest.toOppgave(TsmOppgaveType.valueOf(oppgaveRequest.kilde))
        oppgaveService.opprettSyncOppgave(oppgave)
        call.respond(HttpStatusCode.Accepted)
    }
}
