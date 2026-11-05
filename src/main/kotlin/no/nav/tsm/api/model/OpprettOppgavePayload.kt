package no.nav.tsm.api.model

import java.time.LocalDate
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType

data class OpprettOppgavePayload(
    val personident: String?,
    val tildeltEnhetsnr: String? = null,
    val journalpostId: String? = null,
    val beskrivelse: String?,
    val behandlingstype: String? = null,
    val fristFerdigstillelse: LocalDate,
    val sykmeldingId: String,
    val type: String,
)

fun OpprettOppgavePayload.toOppgave(type: TsmOppgaveType): Oppgave {
    return when (type) {
        TsmOppgaveType.UTENLANDSK ->
            Oppgave.Utenlandsk(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                journalpostId = journalpostId,
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.NASJONAL ->
            Oppgave.Nasjonal(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                journalpostId = journalpostId,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.TILBAKEDATERING ->
            Oppgave.Tilbakedatering(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.FORDELING ->
            Oppgave.Fordeling(
                sykmeldingId = sykmeldingId,
                beskrivelse = beskrivelse,
                behandlingstype = behandlingstype,
                aktivDato = LocalDate.now(),
                journalpostId = journalpostId,
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.BEHANDLINGSDAGER ->
            Oppgave.Behandlingsdager(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.OPPFOLGING ->
            Oppgave.Oppfolging(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                tildeltEnhetsnr = tildeltEnhetsnr,
                journalpostId = journalpostId,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        TsmOppgaveType.INFOTRYGD ->
            Oppgave.Infotrygd(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                journalpostId = journalpostId,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )
    }
}
