package no.nav.tsm.oppgave

import io.ktor.server.application.Application
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.oppgave.client.OppgaveCloudClient
import no.nav.tsm.oppgave.client.OppgaveLocalClient

fun Application.configureOppgaveDependencies() {
    dynamicDependencies {
        cloud { provide(OppgaveCloudClient::class) }
        local { provide(OppgaveLocalClient::class) }
    }
}