package no.nav.tsm.oppgave

import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.ktor.logger

class OppgaveRepository {
    private val logger = logger()

    suspend fun insert(oppgave: Oppgave) = dbQuery { logger.info("Inserting oppgave") }
}
