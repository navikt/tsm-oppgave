package no.nav.tsm.oppgave

import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.BehandlesAvApplikasjon
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.Oppgave.Utenlandsk
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test

class OppgaveRepositoryTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }

    val oppgaveRepository = OppgaveRepository()

    val IDENT = "11111111111"

    @Test
    fun `insert and list utenlandsk oppgave`() = runTest {
        val oppgave =
            Utenlandsk(
                sykmeldingId = UUID.randomUUID().toString(),
                personident = IDENT,
                beskrivelse = null,
                journalpostId = null,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = null,
            )
        oppgaveRepository.insert(oppgave)
        val oppgaver = oppgaveRepository.listByIdent(IDENT)
        oppgaver shouldBe listOf(oppgave)
        oppgaver.first().type shouldBe TsmOppgaveType.UTENLANDSK
        oppgaver.first().oppgaveFelles.behandlesAvApplikasjon shouldBe BehandlesAvApplikasjon.SMD
    }

    @Test
    fun `insert and list Nasjonal oppgave`() = runTest {
        val oppgave =
            Oppgave.Nasjonal(
                sykmeldingId = UUID.randomUUID().toString(),
                personident = IDENT,
                beskrivelse = null,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = null,
                journalpostId = null,
            )
        oppgaveRepository.insert(oppgave)
        val oppgaver = oppgaveRepository.listByIdent(IDENT)
        oppgaver shouldBe listOf(oppgave)
        oppgaver.first().type shouldBe TsmOppgaveType.NASJONAL
        oppgaver.first().oppgaveFelles.behandlesAvApplikasjon shouldBe BehandlesAvApplikasjon.SMR
    }

    @Test
    fun `insert and list Tilbakedatering oppgave`() = runTest {
        val oppgave =
            Oppgave.Tilbakedatering(
                sykmeldingId = UUID.randomUUID().toString(),
                personident = IDENT,
                beskrivelse = null,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = null,
            )
        oppgaveRepository.insert(oppgave)
        val oppgaver = oppgaveRepository.listByIdent(IDENT)
        oppgaver shouldBe listOf(oppgave)
        oppgaver.first().type shouldBe TsmOppgaveType.TILBAKEDATERING
        oppgaver.first().oppgaveFelles.behandlesAvApplikasjon shouldBe BehandlesAvApplikasjon.SMM
    }
}
