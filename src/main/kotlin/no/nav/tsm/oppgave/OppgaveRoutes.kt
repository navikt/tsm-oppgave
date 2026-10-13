package no.nav.tsm.oppgave

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.tsm.oppgave.client.model.OppgaveErrors

fun Route.oppgaveRoutes(
    oppgaveService: OppgaveService,
) {
    get("/api/oppgaver") {
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
                        call.respond(message = "not Fund", status = HttpStatusCode.NotFound)
                    }
                     else -> call.respond(message = "Internal Server Error", status = HttpStatusCode.InternalServerError)
                }
            },
            ifRight = {
                call.respond(it)
            }
        )
    }

}

