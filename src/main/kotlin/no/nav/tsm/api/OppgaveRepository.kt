package no.nav.tsm.api

import java.time.LocalDateTime
import java.util.UUID
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import no.nav.tsm.core.db.OpprettOppgaveTable
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.OppgaveKilde
import no.nav.tsm.core.model.OppgaveRequest
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

class OppgaveRepository {

    suspend fun insert(request: OppgaveRequest.Post) = dbQuery {
        val oppgave = request.oppgave
        OpprettOppgaveTable.insert {
            it[id] = UUID.randomUUID()
            it[personident] = oppgave.personident
            it[tildelt_enhetsnr] =
                when (oppgave) {
                    is Oppgave.Utenlandsk -> oppgave.tildeltEnhetsnr
                    is Oppgave.Oppfolging -> oppgave.tildeltEnhetsnr
                    else -> null
                }
            it[opprettet_av_enhetsnr] = "9999"
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
            it[tema] = "SYM"
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
            it[aktiv_dato] = request.aktivDato.toKotlinLocalDate()
            it[frist_ferdigstillelse] = request.fristFerdigstillelse?.toKotlinLocalDate()
            it[prioritet] = oppgave.oppgaveFelles.prioritet.name
            it[ekstern_referanse_id] = oppgave.eksternReferanseId
            it[created] = LocalDateTime.now().toKotlinLocalDateTime()
            it[oppgave_opprettet] = null
            it[oppgave_id] = null
            it[oppgavekilde] = oppgave.type.toString()
        }
    }

    suspend fun listByEksternReferanseId(eksternId: String): List<OppgaveRequest> = dbQuery {
        OpprettOppgaveTable.selectAll()
            .where { OpprettOppgaveTable.ekstern_referanse_id eq eksternId }
            .map { it.toOppgaveRequest() }
    }

    fun ResultRow.toOppgaveRequest(): OppgaveRequest {
        val kilde = OppgaveKilde.valueOf(this[OpprettOppgaveTable.oppgavekilde])
        val personident = this[OpprettOppgaveTable.personident]
        val eksternReferanseId = this[OpprettOppgaveTable.ekstern_referanse_id]
        val beskrivelse = this[OpprettOppgaveTable.beskrivelse]
        val oppgaveId = this[OpprettOppgaveTable.oppgave_id]?.toString()
        val oppgave =
            when (kilde) {
                OppgaveKilde.UTENLANDSK ->
                    Oppgave.Utenlandsk(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.NASJONAL ->
                    Oppgave.Nasjonal(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        journalpostId = this[OpprettOppgaveTable.journalpost_id],
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.TILBAKEDATERING ->
                    Oppgave.Tilbakedatering(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.FORDELING ->
                    Oppgave.Fordeling(
                        eksternReferanseId = eksternReferanseId,
                        personident = personident,
                        beskrivelse = beskrivelse,
                        behandlingstype = this[OpprettOppgaveTable.behandlingstype],
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.BEHANDLINGSDAGER ->
                    Oppgave.Behandlingsdager(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        journalpostId = this[OpprettOppgaveTable.journalpost_id],
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.OPPFOLGING ->
                    Oppgave.Oppfolging(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        tildeltEnhetsnr = this[OpprettOppgaveTable.tildelt_enhetsnr],
                        journalpostId = this[OpprettOppgaveTable.journalpost_id],
                        oppgaveId = oppgaveId,
                    )
                OppgaveKilde.INFOTRYGD ->
                    Oppgave.Infotrygd(
                        eksternReferanseId = eksternReferanseId,
                        personident = requireNotNull(personident),
                        beskrivelse = beskrivelse,
                        journalpostId = this[OpprettOppgaveTable.journalpost_id],
                        oppgaveId = oppgaveId,
                    )
            }

        return OppgaveRequest.Post(
            oppgave = oppgave,
            aktivDato = this[OpprettOppgaveTable.aktiv_dato].toJavaLocalDate(),
            fristFerdigstillelse =
                this[OpprettOppgaveTable.frist_ferdigstillelse]?.toJavaLocalDate(),
        )
    }
}
