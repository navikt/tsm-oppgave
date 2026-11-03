package no.nav.tsm.api.model

import java.time.LocalDate
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.OppgaveType

data class CreateOppgavePayload(
    val personident: String?,
    val tildeltEnhetsnr: String?,
    val journalpostId: String?,
    val beskrivelse: String?,
    val behandlingstype: String?,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate,
    val sykmeldingId: String,
    val kilde: String,
)

fun CreateOppgavePayload.toOppgave(kilde: OppgaveType): Oppgave {
    return when (kilde) {
        OppgaveType.UTENLANDSK ->
            Oppgave.Utenlandsk(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.NASJONAL ->
            Oppgave.Nasjonal(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                journalpostId = journalpostId,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.TILBAKEDATERING ->
            Oppgave.Tilbakedatering(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.FORDELING ->
            Oppgave.Fordeling(
                sykmeldingId = sykmeldingId,
                beskrivelse = beskrivelse,
                behandlingstype = behandlingstype,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.BEHANDLINGSDAGER ->
            Oppgave.Behandlingsdager(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.OPPFOLGING ->
            Oppgave.Oppfolging(
                sykmeldingId = sykmeldingId,
                personident = requireNotNull(personident),
                beskrivelse = beskrivelse,
                tildeltEnhetsnr = tildeltEnhetsnr,
                journalpostId = journalpostId,
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = fristFerdigstillelse,
            )

        OppgaveType.INFOTRYGD ->
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
