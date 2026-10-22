package no.nav.tsm.oppgave.client

import arrow.core.Either
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.OpprettOppgaveRequest
import no.nav.tsm.oppgave.client.model.OpprettOppgaveResponse

class OppgaveLocalClient : OppgaveClient {

    override suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun createOppgave(
        oppgave: OpprettOppgaveRequest
    ): Either<OppgaveErrors, OpprettOppgaveResponse> {
        TODO("Not yet implemented")
    }
}
