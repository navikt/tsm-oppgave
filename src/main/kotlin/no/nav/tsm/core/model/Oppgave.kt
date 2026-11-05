package no.nav.tsm.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

enum class TsmOppgaveType {
    UTENLANDSK,
    NASJONAL,
    TILBAKEDATERING,
    FORDELING,
    BEHANDLINGSDAGER,
    OPPFOLGING,
    INFOTRYGD,
}

enum class OppgaveFelles(
    val oppgaveType: Oppgavetype,
    val prioritet: Prioritet,
    val behandlesAvApplikasjon: BehandlesAvApplikasjon,
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
    ),
}

sealed interface Oppgave {
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY) val type: TsmOppgaveType
    val oppgaveFelles: OppgaveFelles
    val sykmeldingId: String
    val beskrivelse: String?
    val oppgaveId: String?
    val aktivDato: LocalDate
    val status: String?
    val fristFerdigstillelse: LocalDate?

    data class Utenlandsk(
        override val sykmeldingId: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val versjon: Int? = null,
        val personident: String,
        val journalpostId: String?,
        val endretAvEnhetsnr: String? = null,
    ) : Oppgave {
        val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi
        val behandlingstype: String = Behandlingstype.UTLAND.verdi
        override val type = TsmOppgaveType.UTENLANDSK
        override val oppgaveFelles = OppgaveFelles.Utenlandsk
    }

    data class Nasjonal(
        override val sykmeldingId: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val versjon: Int? = null,
        val personident: String,
        val tilordnetRessurs: String? = null,
        val journalpostId: String?,
        val endretAvEnhetsnr: String? = null,
    ) : Oppgave {
        override val type = TsmOppgaveType.NASJONAL
        override val oppgaveFelles = OppgaveFelles.Nasjonal
    }

    data class Tilbakedatering(
        override val sykmeldingId: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val versjon: Int? = null,
        val personident: String,
        val tilordnetRessurs: String? = null,
    ) : Oppgave {
        val behandlingsType: String = Behandlingstype.TILBAKEDATERING.verdi
        override val type = TsmOppgaveType.TILBAKEDATERING
        override val oppgaveFelles = OppgaveFelles.Tilbakedatering
    }

    data class Fordeling(
        override val sykmeldingId: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val journalpostId: String?,
        val behandlingstype: String?,
    ) : Oppgave {
        override val type = TsmOppgaveType.FORDELING
        override val oppgaveFelles = OppgaveFelles.Fordeling
    }

    data class Behandlingsdager(
        override val sykmeldingId: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val personident: String,
        val journalpostId: String? = null,
    ) : Oppgave {
        val behandlingstema: String = Behandlingstema.BEHANDLINGSDAGER.verdi
        override val type = TsmOppgaveType.BEHANDLINGSDAGER
        override val oppgaveFelles = OppgaveFelles.Behandlingsdager
    }

    data class Oppfolging(
        override val beskrivelse: String?,
        override val sykmeldingId: String,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val personident: String,
        val tildeltEnhetsnr: String?,
        val journalpostId: String?,
    ) : Oppgave {
        override val type = TsmOppgaveType.OPPFOLGING
        override val oppgaveFelles = OppgaveFelles.Oppfolging
    }

    data class Infotrygd(
        override val beskrivelse: String?,
        override val sykmeldingId: String,
        override val oppgaveId: String? = null,
        override val status: String? = null,
        override val aktivDato: LocalDate,
        override val fristFerdigstillelse: LocalDate?,
        val personident: String,
        val journalpostId: String?,
    ) : Oppgave {
        override val type = TsmOppgaveType.INFOTRYGD
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
}

enum class Behandlingstype(val verdi: String) {
    UTLAND("ae0106"),
    TILBAKEDATERING("ae0239"),
}

enum class Behandlingstema(val verdi: String) {
    BEHANDLINGSDAGER("ab0351")
}

enum class TildeltEnhetsnr(val verdi: String) {
    UTLAND("0393")
}

enum class Oppgavetype {
    JFR,
    BEH_EL_SYM,
    FDR,
}
