package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave

sealed interface OppgaveClient {
    enum class OppgaveErrors {
        NotFound,
        Unknown,
    }

    suspend fun getOppgaveById(oppgaveId: String): Either<OppgaveErrors, Oppgave>
}