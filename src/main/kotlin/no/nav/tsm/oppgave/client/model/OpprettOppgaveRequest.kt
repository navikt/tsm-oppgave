package no.nav.tsm.oppgave.client.model

import java.time.LocalDate

data class OpprettOppgaveRequest(
    val tildeltEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val aktoerId: String? = null,
    val journalpostId: String? = null,
    val behandlesAvApplikasjon: String? = null,
    val tilordnetRessurs: String? = null,
    val beskrivelse: String? = null,
    val tema: String? = null,
    val oppgavetype: String,
    val behandlingstype: String? = null,
    val behandlingstema: String? = null,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate? = null,
    val prioritet: String,
)