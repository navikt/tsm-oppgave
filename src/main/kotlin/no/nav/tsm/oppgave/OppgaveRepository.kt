package no.nav.tsm.oppgave

import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.core.model.OppgaveGrunnlag
import no.nav.tsm.ktor.logger

class OppgaveRepository {
    private val logger = logger()

    suspend fun insert(oppgaveGrunnlag: OppgaveGrunnlag) = dbQuery {
        logger.info("Inserting oppgave")
    }
}
