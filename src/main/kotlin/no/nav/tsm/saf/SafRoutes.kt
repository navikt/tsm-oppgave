package no.nav.tsm.saf

import arrow.core.raise.context.bind
import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import no.nav.tsm.ktor.logger
import no.nav.tsm.saf.client.model.SafErrors

fun Route.safRoutes(
    safService: SafService
) {
    val logger = logger()

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