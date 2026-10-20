package no.nav.tsm.core.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.datetime.datetime

object JournalpostTable : Table("journalpost") {
    val sykmeldingId = javaUUID("sykmelding_id")
    val created = datetime("created").nullable()
    val journalpostId = text("journalpost_id")
}