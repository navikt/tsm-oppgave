package no.nav.tsm.oppgave.client.model

import java.time.LocalDate

// TODO: flere verdier skal her
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
) {
    val opprettetAvEnhetsnr: String = "9999"
    val tema: String = "SYM"
}
