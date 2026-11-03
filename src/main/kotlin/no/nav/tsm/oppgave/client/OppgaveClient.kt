package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.client.model.OppgaveClientErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.OpprettOppgaveRequest
import no.nav.tsm.oppgave.client.model.OpprettOppgaveResponse

sealed interface OppgaveClient {

    suspend fun getOppgaver(ident: String): Either<OppgaveClientErrors, List<OppgaveResponse>>

    suspend fun createOppgave(
        oppgave: OpprettOppgaveRequest
    ): Either<OppgaveClientErrors, OpprettOppgaveResponse>
}
