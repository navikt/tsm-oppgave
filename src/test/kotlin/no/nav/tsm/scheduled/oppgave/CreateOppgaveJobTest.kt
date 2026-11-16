package no.nav.tsm.scheduled.oppgave

import io.mockk.mockk
import no.nav.tsm.oppgave.OppgaveRepository
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.saf.client.SafClient
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository
import no.nav.tsm.utils.WithPostgresql

class CreateOppgaveJobTest : WithPostgresql() {

    init {
        runMigrations(true)
        connect()
    }

    val safClient = mockk<SafClient>()
    val oppgaveClient = mockk<OppgaveClient>()
    val oppgaveRepository = OppgaveRepository()
    val createOppgaveRepository = CreateOppgaveRepository()
    val oppgaveService = OppgaveService(oppgaveClient, oppgaveRepository, createOppgaveRepository)
}
