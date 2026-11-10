package no.nav.tsm.oppgave

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.oppgave.client.model.OppgaveClientErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.toOpprettOppgaveRequest

enum class OppgaveServiceErrors {
    Conflict,
    NotFound,
    NotSupported,
    Error,
}

class OppgaveService(
    private val oppgaveClient: OppgaveClient,
    private val oppgaveRepository: OppgaveRepository,
) {

    @WithSpan
    suspend fun getOppgaver(ident: String): Either<OppgaveClientErrors, List<OppgaveResponse>> {
        // Todo deal with errors?
        return oppgaveClient.getOppgaver(ident)
    }

    @WithSpan
    suspend fun opprettOppgave(oppgave: Oppgave): Either<OppgaveServiceErrors, Oppgave> {
        if (oppgave.type != TsmOppgaveType.BEHANDLINGSDAGER) {
            return OppgaveServiceErrors.NotSupported.left()
        }

        val createdOppgave = oppgaveRepository.insert(oppgave)
        return createdOppgave.right()
    }

    @WithSpan
    suspend fun oppretAsyncOppgave(oppgave: Oppgave): Either<OppgaveServiceErrors, Oppgave> {
        val insertOppgave = oppgaveRepository.insert(oppgave)
        return TODO()
    }

    @WithSpan
    suspend fun opprettSyncOppgave(oppgave: Oppgave): Either<OppgaveServiceErrors, Oppgave> {
        val insertOppgave = oppgaveRepository.insert(oppgave)
        oppgaveClient.createOppgave(oppgave = oppgave.toOpprettOppgaveRequest())
        return TODO()
    }
}
