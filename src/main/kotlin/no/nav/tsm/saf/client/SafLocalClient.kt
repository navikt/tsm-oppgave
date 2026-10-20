package no.nav.tsm.saf.client

import arrow.core.Either
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors

class SafLocalClient: SafClient {
    override suspend fun getJournalpost(eksternReferanseId: String): Either<SafErrors, Journalpost> {
        TODO("Not yet implemented")
    }
}