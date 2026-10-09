package no.nav.tsm.oppgave.client

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.jackson3.*
import io.ktor.server.plugins.di.annotations.*
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.ktor.auth.texas.Texas
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.Oppgave

class OppgaveCloudClient(
    @Named("RetryHttpClient") httpClient: HttpClient,
    private val texasClient: Texas,
    private val environment: Environment,
): OppgaveClient {
    private val logger = logger()

    private val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) { jackson {} }
    }

    data class OppgaveResponse(val id: String)

    @WithSpan
    override suspend fun getOppgaver(xCorrelationId: String): Either<OppgaveClient.OppgaveErrors, Oppgave> {
        val span = Span.current()
        val (accessToken) = this.getToken()

        val response =
            httpClient.get("${environment.external().oppgaveApi}/api/v1/oppgaver") {
                header("Authorization", "Bearer $accessToken")
                header("X-Correlation-ID", xCorrelationId)
                contentType(ContentType.Application.Json)
            }

        return when {
            response.status.isSuccess() -> {
                span.setAttribute("client.outcome", "ok")
                mapToOppgave(response.body()).right()
            }

            response.status == HttpStatusCode.NotFound -> {
                span.setAttribute("client.outcome", "not-found")
                OppgaveClient.OppgaveErrors.NotFound.left()
            }

            else -> {
                span.setAttribute("client.outcome", response.status.toString())
                logger.error("oppgave-api responded with status ${response.status}")
                OppgaveClient.OppgaveErrors.Unknown.left()
            }
        }

    }

    private fun mapToOppgave(oppgave: OppgaveResponse): Oppgave {
        return Oppgave(
            id = oppgave.id,
        )
    }

    private suspend fun getToken() = texasClient.entraIdToken("tsm", "zara")
}