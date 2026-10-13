package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave
import no.nav.tsm.oppgave.OppgaveErrors

sealed interface OppgaveClient {

    suspend fun getOppgave(oppgaveId: Int, xCorrelationId: String): Either<OppgaveErrors, Oppgave>

    suspend fun getOppgaver(fnr: String): Either<OppgaveErrors, List<Oppgave>>

    suspend fun createOppgave(oppgave: Oppgave): Either<OppgaveErrors, Oppgave>
}