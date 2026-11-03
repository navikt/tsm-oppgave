package no.nav.tsm.oppgave.client.model

import no.nav.tsm.core.model.Oppgave
import java.time.LocalDate

data class OpprettOppgaveRequest(
    val personident: String?,
    val tildeltEnhetsnr: String?,
    val journalpostId: String?,
    val beskrivelse: String?,
    val behandlingstype: String?,
    val behandlingstema: String?,
    val behandlesAvApplikasjon: String,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String,
    val oppgaveType: String,
    val prioritet: String,
) {
    val opprettetAvEnhetsnr: String = "9999"
    val tema: String = "SYM"
}

fun Oppgave.toOpprettOppgaveRequest(): OpprettOppgaveRequest {
    return OpprettOppgaveRequest(
        personident = when (this) {
            is Oppgave.Nasjonal -> personident
            is Oppgave.Behandlingsdager -> personident
            is Oppgave.Oppfolging -> personident
            is Oppgave.Infotrygd -> personident
            is Oppgave.Utenlandsk -> personident
            is Oppgave.Tilbakedatering -> personident
            else -> null
        },
        tildeltEnhetsnr = when (this) {
            is Oppgave.Utenlandsk -> tildeltEnhetsnr
            is Oppgave.Oppfolging -> tildeltEnhetsnr
            else -> null
        },
        journalpostId = when (this) {
            is Oppgave.Nasjonal -> journalpostId
            is Oppgave.Behandlingsdager -> journalpostId
            is Oppgave.Oppfolging -> journalpostId
            is Oppgave.Infotrygd -> journalpostId
            else -> null
        },
        beskrivelse = beskrivelse,
        behandlingstype = when (this) {
            is Oppgave.Utenlandsk -> behandlingstype
            is Oppgave.Tilbakedatering -> behandlingsType
            is Oppgave.Fordeling -> behandlingstype
            else -> null
        },
        behandlingstema = when (this) {
            is Oppgave.Behandlingsdager -> behandlingstema
            else -> null
        },
        behandlesAvApplikasjon = oppgaveFelles.behandlesAvApplikasjon.name,
        aktivDato = aktivDato,
        fristFerdigstillelse = fristFerdigstillelse,
        eksternReferanseId = sykmeldingId,
        oppgaveType = oppgaveFelles.oppgaveType.name,
        prioritet = oppgaveFelles.prioritet.name
    )
}
