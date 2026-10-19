package no.nav.tsm.saf.client

import arrow.core.Either
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors

sealed interface SafClient {

    suspend fun getJournalpost(eksternReferanseId: String): Either<SafErrors, List<Journalpost>>
}