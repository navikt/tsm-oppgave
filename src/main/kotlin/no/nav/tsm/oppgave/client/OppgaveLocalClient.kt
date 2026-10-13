package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave

class OppgaveLocalClient(): OppgaveClient {
    override suspend fun getOppgave(
        oppgaveId: Int,
        xCorrelationId: String
    ): Either<OppgaveClient.OppgaveErrors, Oppgave> {
        TODO("Not yet implemented")
    }

    override suspend fun getOppgaver(xCorrelationId: String): Either<OppgaveClient.OppgaveErrors, List<Oppgave>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOppgave(oppgave: Oppgave): Either<OppgaveClient.OppgaveErrors, Oppgave> {
        TODO("Not yet implemented")
    }
}