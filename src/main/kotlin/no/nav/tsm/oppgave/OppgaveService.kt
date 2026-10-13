package no.nav.tsm.oppgave

import arrow.core.Either
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse

class OppgaveService(private val oppgaveClient: OppgaveClient) {

    @WithSpan
    suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>>  {
        /*
        * sjekke tilganger
        * er det en saksbehandler
        * er det m2m
        * håndtere eventuelle oss problem
        * */

        return oppgaveClient.getOppgaver(ident)
    }
}