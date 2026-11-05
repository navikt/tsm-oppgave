package no.nav.tsm.api.model

import io.kotest.matchers.shouldBe
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import no.nav.tsm.core.model.Behandlingstema
import no.nav.tsm.core.model.Behandlingstype
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TildeltEnhetsnr
import no.nav.tsm.core.model.TsmOppgaveType
import org.junit.Test

class ToOppgaveTest {
    val IDENT = "11111111111"
    val SYKMELDING_ID = "SYKMELDING_ID"

    @Test
    fun `map Utenlandsk request to Oppgave`() = runTest {
        val utenlandskRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "UTENLANDSK",
            )
        val oppgave = utenlandskRequest.toOppgave(TsmOppgaveType.UTENLANDSK) as Oppgave.Utenlandsk
        oppgave.type shouldBe TsmOppgaveType.UTENLANDSK
        oppgave.personident shouldBe utenlandskRequest.personident
        oppgave.sykmeldingId shouldBe utenlandskRequest.sykmeldingId
        oppgave.journalpostId shouldBe utenlandskRequest.journalpostId
        oppgave.tildeltEnhetsnr shouldBe TildeltEnhetsnr.UTLAND.verdi
        oppgave.behandlingstype shouldBe Behandlingstype.UTLAND.verdi
    }

    @Test
    fun `map Nasjonal request to Oppgave`() = runTest {
        val nasjonalOppgaveRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "NASJONAL",
            )
        val oppgave = nasjonalOppgaveRequest.toOppgave(TsmOppgaveType.NASJONAL) as Oppgave.Nasjonal
        oppgave.type shouldBe TsmOppgaveType.NASJONAL
        oppgave.personident shouldBe nasjonalOppgaveRequest.personident
        oppgave.sykmeldingId shouldBe nasjonalOppgaveRequest.sykmeldingId
        oppgave.journalpostId shouldBe nasjonalOppgaveRequest.journalpostId
    }

    @Test
    fun `map Tilbakedatering request to Oppgave`() = runTest {
        val tilbakedateringRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "TILBAKEDATERING",
            )
        val oppgave =
            tilbakedateringRequest.toOppgave(TsmOppgaveType.TILBAKEDATERING)
                as Oppgave.Tilbakedatering
        oppgave.type shouldBe TsmOppgaveType.TILBAKEDATERING
        oppgave.personident shouldBe tilbakedateringRequest.personident
        oppgave.sykmeldingId shouldBe tilbakedateringRequest.sykmeldingId
        oppgave.behandlingsType shouldBe Behandlingstype.TILBAKEDATERING.verdi
    }

    @Test
    fun `map Fordeling request to Oppgave`() = runTest {
        val fordelingsRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = "test",
                type = "FORDELING",
            )
        val oppgave = fordelingsRequest.toOppgave(TsmOppgaveType.FORDELING) as Oppgave.Fordeling
        oppgave.type shouldBe TsmOppgaveType.FORDELING
        oppgave.sykmeldingId shouldBe fordelingsRequest.sykmeldingId
        oppgave.behandlingstype shouldBe "test"
    }

    @Test
    fun `map Behandlingsdager request to Oppgave`() = runTest {
        val behandlingsdagerRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "BEHANDLINGSDAGER",
            )
        val oppgave =
            behandlingsdagerRequest.toOppgave(TsmOppgaveType.BEHANDLINGSDAGER)
                as Oppgave.Behandlingsdager
        oppgave.type shouldBe TsmOppgaveType.BEHANDLINGSDAGER
        oppgave.sykmeldingId shouldBe behandlingsdagerRequest.sykmeldingId
        oppgave.behandlingstema shouldBe Behandlingstema.BEHANDLINGSDAGER.verdi
    }

    @Test
    fun `map Oppfolging request to Oppgave`() = runTest {
        val oppfolgingRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "OPPFOLGING",
            )
        val oppgave = oppfolgingRequest.toOppgave(TsmOppgaveType.OPPFOLGING) as Oppgave.Oppfolging
        oppgave.type shouldBe TsmOppgaveType.OPPFOLGING
        oppgave.sykmeldingId shouldBe oppfolgingRequest.sykmeldingId
    }

    @Test
    fun `map Infotrygd request to Oppgave`() = runTest {
        val infotrygdRequest =
            OpprettOppgavePayload(
                personident = IDENT,
                sykmeldingId = SYKMELDING_ID,
                fristFerdigstillelse = LocalDate.now(),
                beskrivelse = null,
                tildeltEnhetsnr = null,
                journalpostId = null,
                behandlingstype = null,
                type = "INFOTRYGD",
            )
        val oppgave = infotrygdRequest.toOppgave(TsmOppgaveType.INFOTRYGD) as Oppgave.Infotrygd
        oppgave.type shouldBe TsmOppgaveType.INFOTRYGD
        oppgave.sykmeldingId shouldBe infotrygdRequest.sykmeldingId
    }
}
