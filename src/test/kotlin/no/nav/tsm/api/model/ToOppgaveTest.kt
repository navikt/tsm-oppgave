package no.nav.tsm.api.model

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.Behandlingstype
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TildeltEnhetsnr
import no.nav.tsm.core.model.TsmOppgaveType
import org.junit.Test
import java.time.LocalDate

class ToOppgaveTest {
    val IDENT = "11111111111"
    val SYKMELDING_ID = "SYKMELDING_ID"

    @Test
    fun `map Utenlandsk request to Oppgave`() = runTest {
        val utenlandskRequest = OpprettOppgavePayload(
            personident = IDENT,
            sykmeldingId = SYKMELDING_ID,
            fristFerdigstillelse = LocalDate.now(),
            beskrivelse = null,
            tildeltEnhetsnr = null,
            journalpostId = null,
            behandlingstype = null,
            kilde = "UTENLANDSK",
        )
        val oppgave = utenlandskRequest.toOppgave(TsmOppgaveType.UTENLANDSK) as Oppgave.Utenlandsk
        oppgave.type shouldBe TsmOppgaveType.UTENLANDSK
        oppgave.personident shouldBe utenlandskRequest.personident
        oppgave.sykmeldingId shouldBe utenlandskRequest.sykmeldingId
        oppgave.journalpostId shouldBe utenlandskRequest.journalpostId
        oppgave.tildeltEnhetsnr shouldBe TildeltEnhetsnr.UTLAND.verdi
        oppgave.behandlingstype shouldBe Behandlingstype.UTLAND.verdi

    }
}