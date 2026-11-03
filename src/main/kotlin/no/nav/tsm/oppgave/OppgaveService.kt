package no.nav.tsm.api

import arrow.core.Either
import arrow.core.right
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.oppgave.OppgaveRepository
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse

class OppgaveService(
    private val oppgaveClient: OppgaveClient,
    private val oppgaveRepository: OppgaveRepository,
) {

    @WithSpan
    suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>> {
        return oppgaveClient.getOppgaver(ident)
    }

    @WithSpan
    suspend fun insertOppgave(oppgave: Oppgave): Either<OppgaveErrors, Boolean> {
        val insertOppgave = oppgaveRepository.insert(oppgave)
        return (insertOppgave.insertedCount == 1).right()
    }
}
