package no.nav.tsm.oppgave

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.oppgave.client.OppgaveCloudClient
import no.nav.tsm.oppgave.client.OppgaveLocalClient

fun Application.configureOppgaveDependencies() {
    dynamicDependencies {
        cloud { provide(OppgaveCloudClient::class) }
        local { provide(OppgaveLocalClient::class) }
    }

    dependencies {
        provide(OppgaveService::class)
        provide(OppgaveRepository::class)
    }
}