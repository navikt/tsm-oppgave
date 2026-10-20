package no.nav.tsm.utils.core

import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.test.runTest
import no.nav.tsm.oppgave.OppgaveRepository
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test
import java.util.UUID

class OppgaveRepositoryTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }
    val oppgaveRepository = OppgaveRepository()

    @Test
    fun `insert and get journalpost`() = runTest {
        val sykmeldingId = UUID.randomUUID()
        oppgaveRepository.insert(
            sykmeldingId = sykmeldingId,
            journalpostId = "123456",
        )
        val journalpost = oppgaveRepository.listJournalpost(sykmeldingId)
        journalpost shouldHaveSize 1
    }
}