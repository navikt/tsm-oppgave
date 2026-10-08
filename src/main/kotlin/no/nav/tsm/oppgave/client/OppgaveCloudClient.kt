package no.nav.tsm.oppgave.client

import arrow.core.Either
import arrow.core.right
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.isSuccess
import io.ktor.serialization.jackson3.*
import io.ktor.server.plugins.di.annotations.*
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.ktor.auth.texas.Texas
import no.nav.tsm.oppgave.Oppgave

class OppgaveCloudClient(
    @Named("RetryHttpClient") httpClient: HttpClient,
    private val texasClient: Texas,
): OppgaveClient {

    val baseUrl = "https://oppgave.dev-fss-pub.nais.io/api/v1"

    private val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) { jackson {} }
    }


    @WithSpan
    override suspend fun getOppgaver(xCorrelationId: String): Either<OppgaveClient.OppgaveErrors, Oppgave> {
        val span = Span.current()
        val (accessToken) = this.getToken()

        val response =
            httpClient.get("$baseUrl/oppgaver") {
                header("Authorization", "Bearer $accessToken")
                header("X-Correlation-ID", xCorrelationId)
            }

        return when {
            response.status.isSuccess() -> {
                span.setAttribute("client.outcome", "ok")
                response.body().mapToOppgave()
            }
        }

    }

    private fun mapToOppgave(): Either<OppgaveClient.OppgaveErrors, Oppgave> {

    }

    private suspend fun getToken() = texasClient.entraIdToken("tsm", "zara")
}