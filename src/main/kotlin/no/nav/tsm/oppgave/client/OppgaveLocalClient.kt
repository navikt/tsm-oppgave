package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave

class OppgaveLocalClient(): OppgaveClient {
    override suspend fun getOppgaveById(oppgaveId: String): Either<OppgaveClient.OppgaveErrors, Oppgave> {
        TODO("Not yet implemented")
    }
}