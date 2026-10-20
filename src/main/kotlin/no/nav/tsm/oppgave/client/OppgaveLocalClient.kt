package no.nav.tsm.oppgave.client

import arrow.core.Either
import arrow.core.left
import no.nav.tsm.ktor.logger

import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.OpprettOppgaveRequest
import no.nav.tsm.oppgave.client.model.OpprettOppgaveResponse

class OppgaveLocalClient : OppgaveClient {
    val logger = logger()
    override suspend fun getOppgave(
        oppgaveId: Int,
        xCorrelationId: String
    ): Either<OppgaveErrors, OppgaveResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>> {
        logger.info("HELLOOOOOO")
        return OppgaveErrors.NotFound.left()
    }

    override suspend fun createOppgave(oppgave: OpprettOppgaveRequest): Either<OppgaveErrors, OpprettOppgaveResponse> {
        TODO("Not yet implemented")
    }
}