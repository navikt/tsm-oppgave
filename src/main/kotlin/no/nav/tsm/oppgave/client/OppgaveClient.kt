package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.Oppgave

sealed interface OppgaveClient {
    enum class OppgaveErrors {
        NotFound,
        Unknown,
    }

    suspend fun getOppgaver(xCorrelationId: String): Either<OppgaveErrors, Oppgave>
}