package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave
import no.nav.tsm.oppgave.OppgaveErrors

class OppgaveLocalClient : OppgaveClient {
    override suspend fun getOppgave(
        oppgaveId: Int,
        xCorrelationId: String
    ): Either<OppgaveErrors, Oppgave> {
        TODO("Not yet implemented")
    }

    override suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<Oppgave>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOppgave(oppgave: Oppgave): Either<OppgaveErrors, Oppgave> {
        TODO("Not yet implemented")
    }
}