package no.nav.tsm.oppgave

import arrow.core.Either
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.core.model.OppgaveGrunnlag
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
    suspend fun insertOppgave(oppgaveGrunnlag: OppgaveGrunnlag) {

        oppgaveRepository.insert(oppgaveGrunnlag)
        // håndtere eventuelle greier?
    }
}
