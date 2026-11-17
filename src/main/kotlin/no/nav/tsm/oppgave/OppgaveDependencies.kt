package no.nav.tsm.oppgave

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.oppgave.client.OppgaveCloudClient
import no.nav.tsm.oppgave.client.OppgaveLocalClient
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository

fun Application.configureOppgaveModule() {
    dynamicDependencies {
        cloud { provide(OppgaveCloudClient::class) }
        local { provide(OppgaveLocalClient::class) }
    }
    dependencies {
        provide(OppgaveService::class)
        provide(CreateOppgaveRepository::class)
        provide(OppgaveRepository::class)
    }
}
