package no.nav.tsm.oppgave

import no.nav.tsm.ktor.logger

class OppgaveRepository {
    private val logger = logger()

    suspend fun insert() {
        logger.info("Inserting oppgave")
    }
}