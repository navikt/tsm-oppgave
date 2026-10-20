package no.nav.tsm.oppgave.client.model

import java.time.LocalDate

data class OpprettOppgaveRequest(
    val eksternReferanseId: String? = null,
    val aktoerId: String? = null,
    val opprettetAvEnhetsnr: String? = "9999",
    val beskrivelse: Beskrivelse? = null,
    val behandlesAvApplikasjon: BehandlesAvApplikasjon = BehandlesAvApplikasjon.SMD,
    val journalpostId: String? = null,
    val tema: String? = "SYM",
    val oppgavetype: Oppgavetype = Oppgavetype.JFR,
    val prioritet: Prioritet = Prioritet.KRITISK,
    val aktivDato: LocalDate = LocalDate.now(),
    val behandlingstype: String = Behandlingskategori.INGEN.verdi, // en av disse må være satt
    val behandlingstema: String = Behandlingskategori.INGEN.verdi,
    val fristFerdigstillelse: LocalDate? = null,
    val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi,
    val orgnr: String? = null,
    val mappeId: Int? = null, // settes til 1 i behandlingsdager?
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
    INGEN(""),
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