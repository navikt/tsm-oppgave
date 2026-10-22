package no.nav.tsm.oppgave.client.model

import java.time.LocalDate

data class OpprettOppgaveRequest(
    val personident: String?,
    val orgnr: String?,
    val tildeltEnhetsnr: String?,
    val opprettetAvEnhetsnr: String = "9999",
    val journalpostId: String?,
    val behandlesAvApplikasjon: String?,
    val beskrivelse: String?,
    val tema: String = "SYM",
    val behandlingstema: String?,
    val oppgavetype: String?,
    val behandlingstype: String?,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String?,
    val prioritet: String?,
)
