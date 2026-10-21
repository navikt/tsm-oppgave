package no.nav.tsm.saf

import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf
import no.nav.tsm.ktor.logger
import no.nav.tsm.saf.client.model.SafErrors

fun Application.configrueSafRoutes() {
    val logger = logger()
    val safService: SafService by dependencies

    routing {
        get("/api/saf/journalpost/eksternRef/{eksternReferanseId}") {
            val eksternReferanseId = call.parameters["eksternReferanseId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Mangler eksternReferanseId"
                )

            either {
                safService.getJournalpostByExternalRef(eksternReferanseId).bind()
            }.fold(
                ifLeft = { error ->
                    when {
                        error == SafErrors.FORBIDDEN -> {
                            call.respond(HttpStatusCode.Forbidden, error)
                        }

                        else -> {
                            logger.error("Feil ved henting av journalpost ${eksternReferanseId}, error=$error")
                            call.respond(HttpStatusCode.BadRequest, error)
                        }
                    }
                },
                ifRight = {
                    call.respond(it)
                }
            )
        }

    }

}