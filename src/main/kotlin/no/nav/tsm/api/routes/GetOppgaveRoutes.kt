package no.nav.tsm.api.routes

import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import no.nav.tsm.api.OppgaveService
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.client.model.OppgaveErrors

fun Route.getOppgaveRoutes(oppgaveService: OppgaveService) {
    val log = logger()

    get("/api/oppgaver") {
        log.info("Henter oppgaver")
        val ident =
            call.request.headers["ident"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Mangler ident-header")
        either { oppgaveService.getOppgaver(ident).bind() }
            .fold(
                ifLeft = { error ->
                    when {
                        error == OppgaveErrors.NotFound -> {
                            log.info("Fant ikke oppgave for ident")
                            call.respond(message = "not Fund", status = HttpStatusCode.NotFound)
                        }

                        else -> {
                            log.error(
                                "Something went wrong when fetching oppgaver. Got error $error"
                            )
                            call.respond(
                                message = "Internal Server Error",
                                status = HttpStatusCode.InternalServerError,
                            )
                        }
                    }
                },
                ifRight = { call.respond(it) },
            )
    }
}
