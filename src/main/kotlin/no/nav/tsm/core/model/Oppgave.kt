package no.nav.tsm.core.model

import java.time.LocalDate

data class OppgaveGrunnlag(
    val id: String,
    val journalpostId: String,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String?,
    val personident: String,
    val orgnummer: String,
    val opprettetAvEnhetsnr: String = "9999",
    val tema: String = "SYM",
)

sealed interface Oppgave {
    val oppgaveGrunnlag: OppgaveGrunnlag
    val behandlesAvApplikasjon: BehandlesAvApplikasjon?
    val oppgavetype: Oppgavetype?
    val prioritet: Prioritet

    data class Utenlandsk(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val behandlingstype: String = Behandlingstype.UTLAND.verdi,
        val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi,
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.JFR
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.SMD
    }

    data class Nasjonal(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val beskrivelse: String = "Manuell registrering av sykmelding mottatt på papir",
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.JFR
        override val prioritet = Prioritet.HOY
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.SMR
    }

    data class Tilbakedatering(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val beskrivelse: String,
        val behandlingsType: String = Behandlingstype.TILBAKEDATERING.verdi,
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.BEH_EL_SYM
        override val prioritet = Prioritet.HOY
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.SMM
    }

    data class Fordeling(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val behandlingsType: String?,
        val beskrivelse: String =
            "Registrer manuelt mottatt papirsykmelding. \n" +
                "Når journalført på korrekt fødselsnummer: Åpne https://syk-dig.intern.dev.nav.no/registrer-sykmelding \n" +
                "tast inn journalpost-ID, hent og opprett sykmelding. Den digitaliseres og legges automatisk inn i Infotrygd.,",
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.FDR
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }

    data class Behandlingsdager(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val metadata: Map<String, String>? = mapOf(),
        val behandlingstema: String = Behandlingstema.BEHANDLINGSDAGER.verdi,
        val behandlingsType: String = Behandlingstype.ANY.verdi,
        val beskrivelse: String =
            "Manuell behandling av sykmelding grunnet følgende regler: Sykmelding inneholder behandlingsdager (felt 4.4).",
        val mappeId: Int = 1,
        val tildeltEnhetsnr: String = "",
        val temagruppe: String = "ANY",
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.BEH_EL_SYM
        override val prioritet = Prioritet.NORM
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }

    data class Oppfolging(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val metadata: Map<String, String>?,
        val behandlingstema: String = Behandlingstype.ANY.verdi,
        val behandlingsType: String = Behandlingstype.ANY.verdi,
        val beskrivelse: String,
        val mappeId: Int = 1,
        val tildeltEnhetsnr: String,
    ) : Oppgave {
        override val oppgavetype = Oppgavetype.BEH_EL_SYM
        override val prioritet = Prioritet.HOY
        override val behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22
    }
}

enum class BehandlesAvApplikasjon {
    SMD,
    SMR,
    FS22,
    SMM,
}

enum class Prioritet {
    HOY,
    NORM,
    LAV,
    KRITISK,
}

enum class Behandlingstype(val verdi: String) {
    UTLAND("ae0106"),
    TILBAKEDATERING("ae0239"),
    ANY("ANY"),
}

enum class Behandlingstema(val verdi: String) {
    BEHANDLINGSDAGER("ab0351")
}

enum class TildeltEnhetsnr(val verdi: String) {
    UTLAND("0393"),
    BEHANDLINGSDAGER(""),
}

enum class Oppgavetype {
    JFR,
    BEH_EL_SYM,
    FDR,
}
