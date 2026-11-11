package no.nav.tsm.oppgave

import java.time.LocalDateTime
import java.util.UUID
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import no.nav.tsm.core.db.OppgaveTable
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertReturning
import org.jetbrains.exposed.v1.jdbc.selectAll

class OppgaveRepository {

    suspend fun insert(oppgave: Oppgave) = dbQuery {
        OppgaveTable.insertReturning {
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
                it[behandles_av_applikasjon] = oppgave.oppgaveFelles.behandlesAvApplikasjon.name
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
                it[sykmeldingId] = oppgave.sykmeldingId
                it[created] = LocalDateTime.now().toKotlinLocalDateTime()
                it[oppgave_opprettet] = null
                it[oppgave_id] = null
                it[tsm_oppgavetype] = oppgave.type.toString()
            }
            .single()
            .toOppgave()
    }

    suspend fun listByIdent(ident: String): List<Oppgave> = dbQuery {
        OppgaveTable.selectAll().where { OppgaveTable.personident eq ident }.map { it.toOppgave() }
    }

    suspend fun listBySykmeldingId(sykmeldingId: String): List<Oppgave> = dbQuery {
        OppgaveTable.selectAll()
            .where { OppgaveTable.sykmeldingId eq sykmeldingId }
            .map { it.toOppgave() }
    }

    fun ResultRow.toOppgave(): Oppgave {
        val type = TsmOppgaveType.valueOf(this[OppgaveTable.tsm_oppgavetype])
        val personident = this[OppgaveTable.personident]
        val sykmeldingId = this[OppgaveTable.sykmeldingId]
        val beskrivelse = this[OppgaveTable.beskrivelse]
        val oppgaveId = this[OppgaveTable.oppgave_id]?.toString()
        val aktivDato = this[OppgaveTable.aktiv_dato].toJavaLocalDate()
        val fristFerdigstillelse = this[OppgaveTable.frist_ferdigstillelse]?.toJavaLocalDate()
        val journalpostId = this[OppgaveTable.journalpost_id]
        return when (type) {
            TsmOppgaveType.UTENLANDSK ->
                Oppgave.Utenlandsk(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    journalpostId = journalpostId,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            TsmOppgaveType.NASJONAL ->
                Oppgave.Nasjonal(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            TsmOppgaveType.TILBAKEDATERING ->
                Oppgave.Tilbakedatering(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            TsmOppgaveType.FORDELING ->
                Oppgave.Fordeling(
                    sykmeldingId = sykmeldingId,
                    beskrivelse = beskrivelse,
                    behandlingstype = this[OppgaveTable.behandlingstype],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                    journalpostId = journalpostId,
                )

            TsmOppgaveType.BEHANDLINGSDAGER ->
                Oppgave.Behandlingsdager(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            TsmOppgaveType.OPPFOLGING ->
                Oppgave.Oppfolging(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    tildeltEnhetsnr = this[OppgaveTable.tildelt_enhetsnr],
                    journalpostId = this[OppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )

            TsmOppgaveType.INFOTRYGD ->
                Oppgave.Infotrygd(
                    sykmeldingId = sykmeldingId,
                    personident = requireNotNull(personident),
                    beskrivelse = beskrivelse,
                    journalpostId = this[OppgaveTable.journalpost_id],
                    oppgaveId = oppgaveId,
                    aktivDato = aktivDato,
                    fristFerdigstillelse = fristFerdigstillelse,
                )
        }
    }
}
