package no.nav.tsm.utils.core

import no.nav.tsm.oppgave.OppgaveRepository
import no.nav.tsm.utils.WithPostgresql
import org.junit.Test

class OppgaveRepositoryTest : WithPostgresql() {
    init {
        runMigrations(true)
        connect()
    }

    val oppgaveRepository = OppgaveRepository()

    @Test fun `insert and get oppgave`() {}
}
