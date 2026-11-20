package no.nav.tsm.oppgave

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.DefaultAsserter.fail
import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.oppgave.client.model.OppgaveClientErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.saf.client.SafClient
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test

class OppgaveServiceTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }

    val oppgaveClient = mockk<OppgaveClient>()
    val safClient = mockk<SafClient>()
    val oppgaveRepository = OppgaveRepository()
    val createOppgaveRepository = CreateOppgaveRepository()
    val oppgaveService =
        OppgaveService(oppgaveClient, safClient, oppgaveRepository, createOppgaveRepository)

    @Test
    fun `should save BEHANDLINGSDAGER oppgave`() = runTest {
        val oppgaveId = oppgaveService.storeOppgave(behandlingsdagerOppgave)
        oppgaveId.fold(
            ifLeft = { error: OppgaveServiceErrors ->
                fail("oppgave should be saved and not return any errors. Got $error")
            },
            ifRight = {
                oppgaveRepository
                    .listBySykmeldingId(behandlingsdagerOppgave.sykmeldingId)
                    .single() shouldBe behandlingsdagerOppgave
            },
        )
    }

    @Test
    fun `unsupported Oppgave types should not be saved`() = runTest {
        val oppgave = oppgaveService.storeOppgave(utenlandskOppgave)
        oppgave.fold(
            ifLeft = { error: OppgaveServiceErrors ->
                error shouldBe OppgaveServiceErrors.NotSupported
            },
            ifRight = { oppgave -> fail("oppgave should not have been saved") },
        )
    }

    @Test
    fun `try to create behandlingsdager oppgave`() = runTest {
        val journalpostId = "123456"
        val oppgaveId = oppgaveRepository.insert(behandlingsdagerOppgave)!!
        coEvery { safClient.getJournalpost(behandlingsdagerOppgave.sykmeldingId) } coAnswers
            {
                Journalpost(journalpostId).right()
            }

        coEvery {
            oppgaveClient.createOppgave(any(), behandlingsdagerOppgave.sykmeldingId)
        } coAnswers
            {
                OppgaveResponse(
                        id = 1,
                        oppgavetype = "SYM",
                        aktivDato = LocalDate.now(),
                        prioritet = "HOY",
                        opprettetTidspunkt = OffsetDateTime.now(),
                    )
                    .right()
            }

        val oppgave = oppgaveService.createOppgave(oppgaveId)
        oppgave.fold(
            ifLeft = { error ->
                fail("oppgave should be saved and not return any errors. Got $error")
            },
            ifRight = { oppgave ->
                oppgave.type shouldBe TsmOppgaveType.BEHANDLINGSDAGER
                val behandlingsdagerOppgave = oppgave as Oppgave.Behandlingsdager
                behandlingsdagerOppgave.journalpostId shouldBe journalpostId
            },
        )
    }

    @Test
    fun `create oppgave that is not BEHANDLINGSDAGER oppgave`() = runTest {
        val oppgaveId = oppgaveRepository.insert(utenlandskOppgave)!!
        val oppgave = oppgaveService.createOppgave(oppgaveId)
        oppgave.fold(
            ifLeft = { error -> error shouldBe OppgaveServiceErrors.NotSupported },
            ifRight = { oppgave -> fail("oppgave should not have been saved") },
        )
    }

    @Test
    fun `create BEHANDLINGSDAGER oppgave when no journalpost exists should fail`() = runTest {
        val oppgaveId = oppgaveRepository.insert(behandlingsdagerOppgave)!!
        coEvery { safClient.getJournalpost(behandlingsdagerOppgave.sykmeldingId) } coAnswers
            {
                SafErrors.NOT_FOUND.left()
            }
        val oppgave = oppgaveService.createOppgave(oppgaveId)
        oppgave.fold(
            ifLeft = { error -> error shouldBe OppgaveServiceErrors.Error },
            ifRight = { oppgave -> fail("oppgave should not have been saved") },
        )
    }

    @Test
    fun `create BEHANDLINGSDAGER oppgave with journalpostId should store in database even when oppgaveClient fails`() =
        runTest {
            val journalpostId = "123456"
            val oppgaveId = oppgaveRepository.insert(behandlingsdagerOppgave)!!
            coEvery { safClient.getJournalpost(behandlingsdagerOppgave.sykmeldingId) } coAnswers
                {
                    Journalpost(journalpostId).right()
                }

            coEvery {
                oppgaveClient.createOppgave(any(), behandlingsdagerOppgave.sykmeldingId)
            } coAnswers { OppgaveClientErrors.Unknown.left() }
            val oppgave = oppgaveService.createOppgave(oppgaveId)
            oppgave.fold(
                ifLeft = { error -> error shouldBe OppgaveServiceErrors.Error },
                ifRight = { oppgave -> fail("oppgave should not have been saved") },
            )
            val persistedOppgave = oppgaveRepository.byId(oppgaveId) as Oppgave.Behandlingsdager
            persistedOppgave.journalpostId shouldBe journalpostId
        }
}

val IDENT = "11111111111"
val JOURNALPOSTID = "123456"

val behandlingsdagerOppgave =
    Oppgave.Behandlingsdager(
        sykmeldingId = UUID.randomUUID().toString(),
        beskrivelse = null,
        aktivDato = LocalDate.now(),
        fristFerdigstillelse = LocalDate.now().plusDays(3),
        personident = IDENT,
    )

val utenlandskOppgave =
    Oppgave.Utenlandsk(
        sykmeldingId = UUID.randomUUID().toString(),
        beskrivelse = null,
        aktivDato = LocalDate.now(),
        fristFerdigstillelse = LocalDate.now().plusDays(3),
        personident = IDENT,
        journalpostId = JOURNALPOSTID,
    )
