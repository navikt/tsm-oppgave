package no.nav.tsm.oppgave

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.ktor.logger
import no.nav.tsm.ktor.teamLogger

fun Route.oppgaveRoutes(
    oppgaveService: OppgaveService,
) {
    val logger = logger()

    get("/api/oppgaver") {
        logger.info("Henter oppgaver")
        val ident = call.request.headers["ident"]
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Mangler ident-header"
            )
        either {
            oppgaveService.getOppgaver(ident).bind()
        }.fold(
            ifLeft = { error ->
                 when {
                    error == OppgaveErrors.NotFound -> {
                        logger.info("Fant ikke oppgave for ident")
                        call.respond(message = "not Fund", status = HttpStatusCode.NotFound)
                    }
                     else -> {
                         logger.error("Something went wrong when fetching oppgaver. Got error ${error}")
                         call.respond(message = "Internal Server Error", status = HttpStatusCode.InternalServerError)
                     }
                }
            },
            ifRight = {
                call.respond(it)
            }
        )
    }

}

