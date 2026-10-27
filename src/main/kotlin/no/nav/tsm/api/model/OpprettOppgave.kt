package no.nav.tsm.api.model

import java.time.LocalDate

data class OpprettOppgaveRequest(
    val personident: String?,
    val tildeltEnhetsnr: String?,
    val journalpostId: String?,
    val beskrivelse: String?,
    val behandlingstype: String?,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate,
    val eksternReferanseId: String,
    val kilde: String,
)
