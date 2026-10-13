package no.nav.tsm.oppgave

import arrow.core.raise.either
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.oppgaveRoutes(
    oppgaveService: OppgaveService,
) {
    get("/oppgave") {
        either {
            oppgaveService.getOppgaver(call.parameters["fnr"]!!).bind()
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

