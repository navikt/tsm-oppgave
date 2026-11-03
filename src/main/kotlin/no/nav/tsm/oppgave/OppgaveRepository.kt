package no.nav.tsm.oppgave

import java.time.LocalDateTime
import java.util.UUID
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import no.nav.tsm.core.db.OpprettOppgaveTable
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.OppgaveType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

class OppgaveRepository {

    suspend fun insert(oppgave: Oppgave) = dbQuery {
        OpprettOppgaveTable.insert {
            it[id] = UUID.randomUUID()
            it[personident] =
                when (oppgave) {
                    is Oppgave.Nasjonal -> oppgave.personident
                    is Oppgave.Behandlingsdager -> oppgave.personident
                    is Oppgave.Oppfolging -> oppgave.personident
                    is Oppgave.Infotrygd -> oppgave.personident
                    is Oppgave.Utenlandsk -> oppgave.personident
                    is Oppgave.Tilbakedatering -> oppgave.personident
                    else -> null
                }
            it[tildelt_enhetsnr] =
                when (oppgave) {
                    is Oppgave.Utenlandsk -> oppgave.tildeltEnhetsnr
                    is Oppgave.Oppfolging -> oppgave.tildeltEnhetsnr
                    else -> null
                }
            it[journalpost_id] =
                when (oppgave) {
                    is Oppgave.Nasjonal -> oppgave.journalpostId
                    is Oppgave.Behandlingsdager -> oppgave.journalpostId
                    is Oppgave.Oppfolging -> oppgave.journalpostId
                    is Oppgave.Infotrygd -> oppgave.journalpostId
                    else -> null
                }
            it[behandles_av_applikasjon] = oppgave.oppgaveFelles.behandlesAvApplikasjon?.name
            it[beskrivelse] = oppgave.beskrivelse
            it[behandlingstema] =
                when (oppgave) {
                    is Oppgave.Behandlingsdager -> oppgave.behandlingstema
                    else -> null
                }
            it[oppgavetype] = oppgave.oppgaveFelles.oppgaveType.name
            it[behandlingstype] =
                when (oppgave) {
                    is Oppgave.Utenlandsk -> oppgave.behandlingstype
                    is Oppgave.Tilbakedatering -> oppgave.behandlingsType
                    is Oppgave.Fordeling -> oppgave.behandlingstype
                    else -> null
                }
            it[aktiv_dato] = oppgave.aktivDato.toKotlinLocalDate()
            it[frist_ferdigstillelse] = oppgave.fristFerdigstillelse?.toKotlinLocalDate()
            it[prioritet] = oppgave.oppgaveFelles.prioritet.name
            it[ekstern_referanse_id] = oppgave.sykmeldingId
            it[created] = LocalDateTime.now().toKotlinLocalDateTime()
            it[oppgave_opprettet] = null
            it[oppgave_id] = null
            it[oppgavekilde] = oppgave.type.toString()
        }
    }

    suspend fun listBySykmeldingId(eksternId: String): List<Oppgave> = dbQuery {
        OpprettOppgaveTable.selectAll()
            .where { OpprettOppgaveTable.ekstern_referanse_id eq eksternId }
            .map { it.toOppgaveRequest() }
    }

    fun ResultRow.toOppgaveRequest(): Oppgave {
        val kilde = OppgaveType.valueOf(this[OpprettOppgaveTable.oppgavekilde])
        val personident = this[OpprettOppgaveTable.personident]
        val eksternReferanseId = this[OpprettOppgaveTable.ekstern_referanse_id]
        val beskrivelse = this[OpprettOppgaveTable.beskrivelse]
        val oppgaveId = this[OpprettOppgaveTable.oppgave_id]?.toString()
        val aktivDato = this[OpprettOppgaveTable.aktiv_dato].toJavaLocalDate()
        val fristFerdigstillelse =
            this[OpprettOppgaveTable.frist_ferdigstillelse]?.toJavaLocalDate()
        return when (kilde) {
            OppgaveType.UTENLANDSK ->
                Oppgave.Utenlandsk(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.NASJONAL ->
                Oppgave.Nasjonal(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OpprettOppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.TILBAKEDATERING ->
                Oppgave.Tilbakedatering(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.FORDELING ->
                Oppgave.Fordeling(
                    sykmeldingId = eksternReferanseId,
                    beskrivelse = beskrivelse,
                    behandlingstype = this[OpprettOppgaveTable.behandlingstype],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.BEHANDLINGSDAGER ->
                Oppgave.Behandlingsdager(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OpprettOppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.OPPFOLGING ->
                Oppgave.Oppfolging(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    tildeltEnhetsnr = this[OpprettOppgaveTable.tildelt_enhetsnr],
                    journalpostId = this[OpprettOppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            OppgaveType.INFOTRYGD ->
                Oppgave.Infotrygd(
                    sykmeldingId = eksternReferanseId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OpprettOppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )
        }
    }
}
