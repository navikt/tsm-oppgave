package no.nav.tsm.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.initializeEnvironment
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.oppgave.client.OppgaveCloudClient
import no.nav.tsm.oppgave.client.OppgaveLocalClient

fun Application.configureDependencies() {
    val config = environment.config

    dynamicDependencies {
        cloud { provide(OppgaveCloudClient::class) }
        local { provide(OppgaveLocalClient::class) }
    }

    dependencies {
        provide<Environment> { initializeEnvironment(config) }
        provide(OppgaveService::class)
    }
}