package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave

sealed interface OppgaveClient {
    enum class OppgaveErrors {
        NotFound,
        Unknown,
        Unauthorized,
        Conflict,
    }

    suspend fun getOppgave(oppgaveId: Int, xCorrelationId: String): Either<OppgaveErrors, Oppgave>

    suspend fun getOppgaver(xCorrelationId: String): Either<OppgaveErrors, List<Oppgave>>

    suspend fun createOppgave(oppgave: Oppgave): Either<OppgaveErrors, Oppgave>
}