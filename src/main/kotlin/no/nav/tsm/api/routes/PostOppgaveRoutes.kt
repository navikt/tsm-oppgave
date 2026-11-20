package no.nav.tsm.api.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import no.nav.tsm.api.model.OpprettOppgavePayload
import no.nav.tsm.api.model.toOppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.oppgave.OppgaveServiceErrors

fun Route.postOppgaveRoutes(oppgaveService: OppgaveService) {
    val logger = logger()
    post("/api/oppgave") {
        logger.info("Routing POST /api/oppgave")
        val oppgaveRequest: OpprettOppgavePayload = call.receive()
        val oppgave = oppgaveRequest.toOppgave(TsmOppgaveType.valueOf(oppgaveRequest.type))
        oppgaveService
            .storeOppgave(oppgave)
            .fold(
                ifLeft = { error ->
                    if (error == OppgaveServiceErrors.NotSupported) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "Ikke mulig å opprette oppgave for type ${oppgaveRequest.type}. SykmeldingId: ${oppgaveRequest.sykmeldingId}",
                        )
                    } else {
                        logger.error("Something went really wrong, check logs")
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                },
                ifRight = {
                    call.respond(HttpStatusCode.Accepted)
                },
            )
    }
}
