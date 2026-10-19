package no.nav.tsm.plugins

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.di.resolve
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.initializeEnvironment
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.oppgave.client.OppgaveCloudClient
import no.nav.tsm.oppgave.client.OppgaveLocalClient
import no.nav.tsm.saf.SafService
import no.nav.tsm.saf.client.SafCloudClient
import no.nav.tsm.saf.client.SafLocalClient

fun Application.configureDependencies() {
    val config = environment.config

    dynamicDependencies {
        cloud {
            provide(OppgaveCloudClient::class)
            provide(SafCloudClient::class)
        }
        local {
            provide(OppgaveLocalClient::class)
            provide(SafLocalClient::class)
        }
    }

    dependencies {
        provide<HttpClient> { configureBaseHttpClient() }
        provide<HttpClient>("RetryHttpClient") {
            createExternalApiHttpClient(resolve<HttpClient>())
        }
        provide<Environment> { initializeEnvironment(config) }
        provide(OppgaveService::class)
        provide(SafService::class)
    }
}

private fun configureBaseHttpClient(): HttpClient = HttpClient(Apache5) {}

private fun createExternalApiHttpClient(baseHttpClient: HttpClient) = baseHttpClient.config {
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 5)
        exponentialDelay()
    }
}