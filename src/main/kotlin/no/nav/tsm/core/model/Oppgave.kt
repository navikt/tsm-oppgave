package no.nav.tsm.core.model

import java.time.LocalDate

data class OppgaveGrunnlag(
    val id: String,
    val journalpostId: String,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String?,
    val opprettetAvEnhetsnr: String = "9999",
    val tema: String = "SYM"
)

sealed interface Oppgave {
    val oppgaveGrunnlag : OppgaveGrunnlag
    val behandlesAvApplikasjon: BehandlesAvApplikasjon?
    val oppgavetype: Oppgavetype?
    val prioritet: Prioritet

    data class Utenlandsk(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val personident: String?,
        val behandlingstype: String = Behandlingskategori.UTLAND.verdi,
        val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.JFR
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.SMD
    }

    data class Nasjonal(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val personident: String?,
        val beskrivelse: String = "Manuell registrering av sykmelding mottatt på papir",
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.JFR
        override val prioritet = Prioritet.HOY
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.SMR
    }

    data class Manuell(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val personident: String?,
        val beskrivelse: String,
        val mappeId: Int,
        val metadata: Map<String, String>?,
        val orgnummer: String,
        val tildeltEnhetsnr: String,
        val behandlingsType: String = Behandlingskategori.ANY.verdi,
        val behandlingstema: String = Behandlingskategori.ANY.verdi,
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.BEH_EL_SYM
        override val prioritet = Prioritet.HOY
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }

    data class Behandlingsdager(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val personident: String?,
        val orgnummer: String,
        val metadata: Map<String, String>? = mapOf(),
        val behandlingstema: String = Behandlingskategori.BEHANDLINGSDAGER.verdi,
        val behandlingsType: String = Behandlingskategori.ANY.verdi,
        val beskrivelse: String = "Manuell behandling av sykmelding grunnet følgende regler: Sykmelding inneholder behandlingsdager (felt 4.4).",
        val mappeId: Int = 1,
        val tildeltEnhetsnr: String = "",
        val temagruppe: String = "ANY"
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.BEH_EL_SYM
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }

    data class Fordeling(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val behandlingsType: String = Behandlingskategori.FORDELING.verdi,
        val beskrivelse: String = "Registrer manuelt mottatt papirsykmelding. \n" +
                "Når journalført på korrekt fødselsnummer: Åpne https://syk-dig.intern.dev.nav.no/registrer-sykmelding \n" +
                "tast inn journalpost-ID, hent og opprett sykmelding. Den digitaliseres og legges automatisk inn i Infotrygd.,",
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.FDR
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }
}

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
    FORDELING("ae0106"),
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
