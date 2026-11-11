package no.nav.tsm.scheduled.oppgave.db

import no.nav.tsm.core.db.OppgaveTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object CreateOppgaveTable : Table("create_oppgave") {
    val id = reference("id", OppgaveTable.id)
    val status = text("status")
    val createdTimestamp = timestampWithTimeZone("created_timestamp")
    val eventTimestamp = timestampWithTimeZone("event_timestamp")
    val completedTimestamp = timestampWithTimeZone("completed_timestamp")
}
