package no.nav.tsm.core.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

enum class OppgaveKilde {
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
    val behandlesAvApplikasjon: BehandlesAvApplikasjon?,
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

sealed interface OppgaveRequest {
    val oppgave: Oppgave

    data class Post(
        override val oppgave: Oppgave,
        val aktivDato: LocalDate,
        val fristFerdigstillelse: LocalDate?,
    ) : OppgaveRequest

    data class Patch(
        override val oppgave: Oppgave,
        val oppgaveId: String?,
        val versjon: Int,
        val status: String,
        val endretAvEnhetsnr: String?,
        val tilordnetRessurs: String?,
    ) : OppgaveRequest
}

sealed interface Oppgave {
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY) val type: OppgaveKilde
    val oppgaveFelles: OppgaveFelles
    val personident: String?
    val eksternReferanseId: String?
    val beskrivelse: String?
    val oppgaveId: String?

    data class Utenlandsk(
        override val eksternReferanseId: String?,
        override val personident: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
    ) : Oppgave {
        val tildeltEnhetsnr: String = TildeltEnhetsnr.UTLAND.verdi
        val behandlingstype: String = Behandlingstype.UTLAND.verdi
        override val type = OppgaveKilde.UTENLANDSK
        override val oppgaveFelles = OppgaveFelles.Utenlandsk
    }

    data class Nasjonal(
        override val eksternReferanseId: String?,
        override val personident: String,
        override val beskrivelse: String?,
        val journalpostId: String?,
        override val oppgaveId: String? = null,
    ) : Oppgave {
        override val type = OppgaveKilde.NASJONAL
        override val oppgaveFelles = OppgaveFelles.Nasjonal
    }

    data class Tilbakedatering(
        override val eksternReferanseId: String?,
        override val personident: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
    ) : Oppgave {
        val behandlingsType: String = Behandlingstype.TILBAKEDATERING.verdi
        override val type = OppgaveKilde.TILBAKEDATERING
        override val oppgaveFelles = OppgaveFelles.Tilbakedatering
    }

    data class Fordeling(
        override val eksternReferanseId: String?,
        override val personident: String?,
        override val beskrivelse: String?,
        val behandlingstype: String?,
        override val oppgaveId: String? = null,
    ) : Oppgave {
        override val type = OppgaveKilde.FORDELING
        override val oppgaveFelles = OppgaveFelles.Fordeling
    }

    data class Behandlingsdager(
        override val eksternReferanseId: String?,
        override val personident: String,
        override val beskrivelse: String?,
        override val oppgaveId: String? = null,
        val journalpostId: String? = null,
    ) : Oppgave {
        val behandlingstema: String = Behandlingstema.BEHANDLINGSDAGER.verdi
        override val type = OppgaveKilde.BEHANDLINGSDAGER
        override val oppgaveFelles = OppgaveFelles.Behandlingsdager
    }

    data class Oppfolging(
        override val personident: String,
        override val beskrivelse: String?,
        override val eksternReferanseId: String?,
        override val oppgaveId: String? = null,
        val tildeltEnhetsnr: String?,
        val journalpostId: String?,
    ) : Oppgave {
        override val type = OppgaveKilde.OPPFOLGING
        override val oppgaveFelles = OppgaveFelles.Oppfolging
    }

    data class Infotrygd(
        override val personident: String,
        override val beskrivelse: String?,
        override val eksternReferanseId: String?,
        override val oppgaveId: String? = null,
        val journalpostId: String?,
    ) : Oppgave {
        override val type = OppgaveKilde.INFOTRYGD
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
