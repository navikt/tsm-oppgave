package no.nav.tsm.saf

import arrow.core.Either
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.saf.client.SafClient
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors

class SafService(private val safClient: SafClient) {

    @WithSpan
    suspend fun getJournalpostByExternalRef(eksternReferanseId: String): Either<SafErrors, Journalpost> {
        return safClient.getJournalpost(eksternReferanseId)
    }
}