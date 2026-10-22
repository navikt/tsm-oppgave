package no.nav.tsm.core.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.datetime.datetime

object OpprettOppgaveTable : Table("opprett_oppgave") {
    val id = javaUUID("id")
    val personident = text("personident").nullable()
    val orgnr = text("orgnr").nullable()
    val tildelt_enhetsnr = text("tildelt_enhetsnr").nullable()
    val opprettet_av_enhetsnr = text("opprettet_av_enhetsnr").nullable()
    val journalpost_id = text("journalpost_id")
    val behandles_av_applikasjon = text("behandles_av_applikasjon").nullable()
    val beskrivelse = text("beskrivelse").nullable()
    val tema = text("tema")
    val behandlingstema = text("behandlingstema").nullable()
    val oppgavetype = text("oppgavetype")
    val behandlingstype = text("behandlingstype").nullable()
    val aktiv_dato = datetime("aktiv_dato")
    val frist_ferdigstillelse = datetime("frist_ferdigstillelse").nullable()
    val prioritet = text("prioritet")
    val ekstern_referanse_id = text("ekstern_referanse_id").nullable()
    val created = datetime("created")
    val oppgave_opprettet = datetime("oppgave_opprettet").nullable()
    val oppgave_id = javaUUID("oppgave_id")
}
