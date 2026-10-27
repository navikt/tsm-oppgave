package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.api.model.OpprettOppgaveRequest
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.OpprettOppgaveResponse

sealed interface OppgaveClient {

    suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>>

    suspend fun createOppgave(
        oppgave: OpprettOppgaveRequest
    ): Either<OppgaveErrors, OpprettOppgaveResponse>
}
