package no.nav.tsm.oppgave

import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.Oppgave.Utenlandsk
import no.nav.tsm.oppgave.client.OppgaveLocalClient
import org.junit.Test
import java.time.LocalDate
import java.util.*

class OppgaveServiceTest {
    val oppgaveLocalClient = OppgaveLocalClient()
    val oppgaveRepository = OppgaveRepository()
    val oppgaveService: OppgaveService = OppgaveService(oppgaveLocalClient, oppgaveRepository)

    @Test
    fun `oppretter utenlands oppgave`() = runTest {
        val oppgave = Utenlandsk(
                sykmeldingId = UUID.randomUUID().toString(),
                personident = "11111111111",
                beskrivelse = null,
                journalpostId = null,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = null,
        )
        oppgaveService.opprettOppgave(oppgave)



    }

}