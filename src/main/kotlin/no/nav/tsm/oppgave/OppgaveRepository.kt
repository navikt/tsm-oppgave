package no.nav.tsm.oppgave

import no.nav.tsm.core.db.JournalpostTable
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.ktor.logger
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.*

class OppgaveRepository {
    private val logger = logger()

    suspend fun insert(sykmeldingId: UUID, journalpostId: String) {
        logger.info("Inserting journalpost")
        dbQuery {
            JournalpostTable.insert {
                it[JournalpostTable.sykmeldingId] = sykmeldingId
                it[JournalpostTable.journalpostId] = journalpostId
            }
        }
    }

    suspend fun listJournalpost(sykmeldingId: UUID) = dbQuery {
        JournalpostTable.selectAll().where { ( JournalpostTable.sykmeldingId eq sykmeldingId) }.toList()
    }
}