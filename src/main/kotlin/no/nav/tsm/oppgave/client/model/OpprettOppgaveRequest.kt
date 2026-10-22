package no.nav.tsm.oppgave.client.model

import java.time.LocalDate


// TODO: denne kan kanskje fjernes/endres på
data class OpprettOppgaveRequest(
    val personident: String?,
    val orgnr: String?,
    val tildeltEnhetsnr: String?,
    val opprettetAvEnhetsnr: String = "9999",
    val journalpostId: String,
    val behandlesAvApplikasjon: BehandlesAvApplikasjon?,
    val beskrivelse: Beskrivelse?,
    val tema: String = "SYM",
    val behandlingstema: String,
    val oppgavetype: Oppgavetype?,
    val behandlingstype: String, // en av disse må være satt
    val aktivDato: LocalDate = LocalDate.now(),
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String?,
    val prioritet: Prioritet = Prioritet.NORM,
)

enum class BehandlesAvApplikasjon {
    SMD, // utenlanske
    SMR, // nasjonale
    FS22, // syfosmoppgave og syfosmmanuell -> gosys
    SMM // syfosmmanuell-backend
}

enum class Prioritet {
    HOY,
    NORM,
    LAV,
    KRITISK,
}

enum class Behandlingskategori(val verdi: String) {
    UTLAND("ae0106"),
    BEHANDLINGSDAGER("ab0351"),
    MANUELL("ae0239"),
    ANY("ANY"),
}

enum class TildeltEnhetsnr(val verdi: String) {
    UTLAND("0393"),
    BEHANDLINGSDAGER("")
}

enum class Beskrivelse(val beskrivelse: String) {
    BEHANDLINGSDAGER( "Manuell behandling av sykmelding grunnet følgende regler: Sykmelding inneholder behandlingsdager (felt 4.4)."),
    NASJONAL("Manuell registrering av sykmelding mottatt på papir")
}
enum class Oppgavetype {
    JFR, // journalføringsoppgave, syk-dig-backend
    BEH_EL_SYM, // behandle elektronisk sykmelding
    FDR, // syfosmpapirmottak
}