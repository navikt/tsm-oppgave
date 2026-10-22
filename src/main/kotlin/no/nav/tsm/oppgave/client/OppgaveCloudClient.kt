package no.nav.tsm.oppgave.client

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson3.*
import io.ktor.server.plugins.di.annotations.*
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.util.*
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.toFssCluster
import no.nav.tsm.ktor.auth.texas.Texas
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.client.model.OppgaveErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.OpprettOppgaveRequest
import no.nav.tsm.oppgave.client.model.OpprettOppgaveResponse

class OppgaveCloudClient(
    @Named("RetryHttpClient") httpClient: HttpClient,
    private val texasClient: Texas,
    private val environment: Environment,
) : OppgaveClient {
    private val logger = logger()

    private val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) { jackson {} }
    }

    data class GetOppgaveResponse(val antallTreffTotalt: Int, val oppgaver: List<OppgaveResponse>)

    // TODO: kanskje vi må sende med en liste med identer
    @WithSpan
    override suspend fun getOppgaver(ident: String): Either<OppgaveErrors, List<OppgaveResponse>> {
        val span = Span.current()
        val (accessToken) = this.getToken()
        val xCorrelationId = UUID.randomUUID().toString()

        val response =
            httpClient.get("${environment.external().oppgaveApi}/api/v1/oppgaver") {
                header("Authorization", "Bearer $accessToken")
                header("X-Correlation-ID", xCorrelationId)
                parameter("aktoerId", ident)
                contentType(ContentType.Application.Json)
            }

        logger.info("$xCorrelationId responded with status ${response.status}")

        return when {
            response.status.isSuccess() -> {
                span.setAttribute("client.outcome", "ok")
                response.body<GetOppgaveResponse>().oppgaver.right()
            }

            response.status == HttpStatusCode.NotFound -> {
                span.setAttribute("client.outcome", "not-found")
                OppgaveErrors.NotFound.left()
            }

            response.status == HttpStatusCode.Unauthorized -> {
                span.setAttribute("client.outcome", "Unauthorized")
                OppgaveErrors.Unauthorized.left()
            }

            response.status == HttpStatusCode.BadRequest -> {
                span.setAttribute("client.outcome", "BadRequest")
                OppgaveErrors.BadRequest.left()
            }

            else -> {
                span.setAttribute("client.outcome", response.status.toString())
                logger.error("oppgave-api responded with status ${response.status}")
                OppgaveErrors.Unknown.left()
            }
        }
    }

    @WithSpan
    override suspend fun createOppgave(
        oppgave: OpprettOppgaveRequest
    ): Either<OppgaveErrors, OpprettOppgaveResponse> {
        val span = Span.current()
        TODO()
    }

    private suspend fun getToken() =
        texasClient.entraIdToken(
            "oppgavehandtering",
            "oppgave",
            cluster = environment.runtime.cluster.toFssCluster(),
        )
}
