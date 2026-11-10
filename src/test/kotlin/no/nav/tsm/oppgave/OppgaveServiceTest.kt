package no.nav.tsm.oppgave

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.context.bind
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test
import java.time.LocalDate
import java.util.UUID
import kotlin.test.DefaultAsserter.fail

class OppgaveServiceTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }

    val oppgaveClient = mockk<OppgaveClient>()
    val oppgaveRepository = OppgaveRepository()
    val oppgaveService = OppgaveService(oppgaveClient, oppgaveRepository)

    @Test
    fun `should save BEHANDLINGSDAGER oppgave`() = runTest {
        val oppgave = oppgaveService.opprettOppgave(behandlingsdagerOppgave)
        oppgave.fold(
            ifLeft = { error: OppgaveServiceErrors ->
                fail("oppgave should be saved and not return any errors. Got $error")
            },
            ifRight = { oppgave ->
                oppgave shouldBe behandlingsdagerOppgave
            }
        )
    }

    @Test
    fun `unsupported Oppgave types should not be saved`() = runTest {
        val oppgave = oppgaveService.opprettOppgave(utenlandskOppgave)
        oppgave.fold(
            ifLeft = { error: OppgaveServiceErrors ->
                error shouldBe OppgaveServiceErrors.NotSupported
            },
            ifRight = { oppgave ->
                fail("oppgave should not have been saved")
            }
        )
    }
}

val IDENT = "11111111111"
val JOURNALPOSTID = "123456"

val behandlingsdagerOppgave = Oppgave.Behandlingsdager(
    sykmeldingId = UUID.randomUUID().toString(),
    beskrivelse = null,
    aktivDato = LocalDate.now(),
    fristFerdigstillelse = LocalDate.now().plusDays(3),
    personident = IDENT,
)

val utenlandskOppgave = Oppgave.Utenlandsk(
    sykmeldingId = UUID.randomUUID().toString(),
    beskrivelse = null,
    aktivDato = LocalDate.now(),
    fristFerdigstillelse = LocalDate.now().plusDays(3),
    personident = IDENT,
    journalpostId = JOURNALPOSTID,
)