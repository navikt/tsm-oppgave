package no.nav.tsm.scheduled.oppgave.db

import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveStatus.PENDING
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveStatus.PROCESSING
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.inSubQuery
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption.PostgreSQL.ForUpdate
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption.PostgreSQL.MODE.SKIP_LOCKED
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.updateReturning

class CreateOppgaveRepository {
    @WithSpan
    suspend fun getNext() = dbQuery {
        with(CreateOppgaveTable) {
            updateReturning(
                    returning = listOf(id),
                    where = {
                        id inSubQuery
                            select(id)
                                .where { status eq PENDING.name }
                                .orderBy(createdTimestamp)
                                .limit(1)
                                .forUpdate(ForUpdate(SKIP_LOCKED))
                    },
                ) {
                    it[status] = PROCESSING.name
                    it[eventTimestamp] = OffsetDateTime.now(ZoneOffset.UTC)
                }
                .firstOrNull()
                ?.get(id)
        }
    }

    @WithSpan
    suspend fun updateStatus(id: UUID, newStatus: CreateOppgaveStatus) = dbQuery {
        CreateOppgaveTable.update({ CreateOppgaveTable.id eq id }) {
            it[eventTimestamp] = OffsetDateTime.now(ZoneOffset.UTC)
            it[status] = newStatus.name
        }
    }

    @WithSpan
    suspend fun insert(id: UUID) = dbQuery {
        CreateOppgaveTable.insert {
            it[CreateOppgaveTable.id] = id
            it[status] = PENDING.name
            it[createdTimestamp] = OffsetDateTime.now(ZoneOffset.UTC)
            it[eventTimestamp] = OffsetDateTime.now(ZoneOffset.UTC)
            it[completedTimestamp] = null
        }
    }

    // TODO: lage en funksjon for sett status til completed

    @WithSpan
    suspend fun resetHangingJobs(timestamp: OffsetDateTime): Int {
        return dbQuery {
            CreateOppgaveTable.update({
                (CreateOppgaveTable.status inList
                    listOf(PROCESSING.name, CreateOppgaveStatus.FAILED.name)) and
                    (CreateOppgaveTable.eventTimestamp less timestamp)
            }) {
                it[status] = PENDING.name
                it[eventTimestamp] = OffsetDateTime.now(ZoneOffset.UTC)
            }
        }
    }
}
