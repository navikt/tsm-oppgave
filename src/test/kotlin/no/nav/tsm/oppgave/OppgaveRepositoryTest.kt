package no.nav.tsm.oppgave

import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.test.runTest
import no.nav.tsm.api.OppgaveRepository
import no.nav.tsm.core.model.Oppgave.Utenlandsk
import no.nav.tsm.core.model.OppgaveRequest
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test

class OppgaveRepositoryTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }

    val oppgaveRepository = OppgaveRepository()

    @Test
    fun `insert and list utenlandsk oppgave`() = runTest {
        val eksternReferanseId = UUID.randomUUID().toString()
        val request =
            OppgaveRequest.Post(
                oppgave =
                    Utenlandsk(
                        eksternReferanseId = eksternReferanseId,
                        personident = "11111111111",
                        beskrivelse = "beskrivelse",
                    ),
                aktivDato = LocalDate.of(2026, 1, 15),
                fristFerdigstillelse = LocalDate.of(2026, 1, 20),
            )
        oppgaveRepository.insert(request)
        val oppgaver = oppgaveRepository.listByEksternReferanseId(eksternReferanseId)
        oppgaver shouldBe listOf(request)
    }
}
