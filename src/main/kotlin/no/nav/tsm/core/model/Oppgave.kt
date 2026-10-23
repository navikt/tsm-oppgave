package no.nav.tsm.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class OppgaveGrunnlag(
    val journalpostId: String?,
    val aktivDato: LocalDate,
    val fristFerdigstillelse: LocalDate?,
    val eksternReferanseId: String?,
    val personident: String,
    val orgnummer: String?,
)

enum class OppgaveKilde {
    UTENLANDSK,
    NASJONAL,
    TILBAKEDATERING,
    FORDELING,
    BEHANDLINGSDAGER,
    OPPFOLGING,
}

enum class OppgaveFelles(
    val oppgaveType: Oppgavetype,
    val prioritet: Prioritet,
    val behandlesAvApplikasjon: BehandlesAvApplikasjon?
) {
    Utenlandsk(
        oppgaveType = Oppgavetype.JFR,
        prioritet = Prioritet.NORM,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.SMD,
    ),

    Nasjonal(
        oppgaveType = Oppgavetype.JFR,
        prioritet = Prioritet.HOY,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.SMR,
    ),

    Tilbakedatering(
        oppgaveType = Oppgavetype.BEH_EL_SYM,
        prioritet = Prioritet.HOY,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.SMM,
    ),

    Fordeling(
        oppgaveType = Oppgavetype.FDR,
        prioritet = Prioritet.NORM,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22,
    ),

    Behandlingsdager(
        oppgaveType = Oppgavetype.FDR,
        prioritet = Prioritet.NORM,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22,
    ),

    Oppfolging(
        oppgaveType = Oppgavetype.BEH_EL_SYM,
        prioritet = Prioritet.HOY,
        behandlesAvApplikasjon = BehandlesAvApplikasjon.FS22,
    )
}

sealed interface Oppgave {
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val type: OppgaveKilde

    val oppgaveGrunnlag: OppgaveGrunnlag
    val oppgaveFelles: OppgaveFelles

    val journalpostId: String?
    val aktivDato: LocalDate
    val fristFerdigstillelse: LocalDate?
    val eksternReferanseId: String?
    val personident: String
    val orgnummer: String?
    val oppgaveId: String? // TODO: Object nullable

    data class Utenlandsk(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val behandlingstype: String = Behandlingstype.UTLAND.verdi,
        val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi,
    ) : Oppgave {
        override val type = OppgaveKilde.UTENLANDSK
        override val oppgaveFelles = OppgaveFelles.Utenlandsk
    }

    data class Nasjonal(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val beskrivelse: String = "Manuell registrering av sykmelding mottatt på papir",
    ) : Oppgave {
        override val type = OppgaveKilde.NASJONAL
        override val oppgaveFelles = OppgaveFelles.Nasjonal
    }

    data class Tilbakedatering(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val beskrivelse: String,
        val behandlingsType: String = Behandlingstype.TILBAKEDATERING.verdi,
    ) : Oppgave {
        override val type = OppgaveKilde.TILBAKEDATERING
        override val oppgaveFelles = OppgaveFelles.Tilbakedatering
    }

    data class Fordeling(
        override val oppgaveGrunnlag: OppgaveGrunnlag,
        val behandlingsType: String?,
        val beskrivelse: String =
            "Registrer manuelt mottatt papirsykmelding. \n" +
                "Når journalført på korrekt fødselsnummer: Åpne https://syk-dig.intern.dev.nav.no/registrer-sykmelding \n" +
                "tast inn journalpost-ID, hent og opprett sykmelding. Den digitaliseres og legges automatisk inn i Infotrygd.,",
    ) : Oppgave {
        override val type = OppgaveKilde.FORDELING
        override val oppgaveFelles = OppgaveFelles.Fordeling
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
        override val type = OppgaveKilde.BEHANDLINGSDAGER
        override val oppgaveFelles = OppgaveFelles.Behandlingsdager
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
        override val type = OppgaveKilde.OPPFOLGING
        override val oppgaveFelles = OppgaveFelles.Oppfolging
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