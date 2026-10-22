package no.nav.tsm.saf.client

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson3.jackson
import io.ktor.server.plugins.di.annotations.Named
import io.opentelemetry.api.trace.Span
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.toFssCluster
import no.nav.tsm.ktor.auth.texas.Texas
import no.nav.tsm.ktor.logger
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors

class SafCloudClient(
    @Named("RetryHttpClient") httpClient: HttpClient,
    private val texasClient: Texas,
    private val environment: Environment,
) : SafClient {
    private val logger = logger()
    private val url = environment.external().safApi.url

    private val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) { jackson {} }
    }

    override suspend fun getJournalpost(
        eksternReferanseId: String
    ): Either<SafErrors, Journalpost> {
        val span = Span.current()
        val (accessToken) = this.getToken()

        val query =
            GrapghQLBody(
                query = JOURNALPOST_QUERY,
                variables = GraphQLVariables(eksternReferanseId = eksternReferanseId),
            )
        val response =
            httpClient.post(url) {
                header("Authorization", "Bearer $accessToken")
                setBody(query)
                contentType(ContentType.Application.Json)
            }

        val body = response.body<GraphQLResponse>()

        if (body.errors?.isNotEmpty() == true) {
            body.errors.forEach {
                return when (it.extensions.code) {
                    "forbidden" -> {
                        span.setAttribute("client.outcome", "forbidden")
                        SafErrors.FORBIDDEN.left()
                    }
                    "not_found" -> {
                        span.setAttribute("client.outcome", "not found")
                        SafErrors.NOT_FOUND.left()
                    }
                    "bad_request" -> {
                        span.setAttribute("client.outcome", "bad_request")
                        SafErrors.BAD_REQUEST.left()
                    }
                    "server_error" -> {
                        span.setAttribute("client.outcome", "server_error")
                        SafErrors.SERVER_ERROR.left()
                    }
                    else -> {
                        span.setAttribute("client.outcome", "unknown")
                        logger.error(
                            "saf responded with unhandled error: ${it.message} and code ${it.extensions.code}"
                        )
                        SafErrors.UNKNOWN.left()
                    }
                }
            }
        }

        if (body.data?.journalpost == null) {
            span.setAttribute("client.outcome", "unknown")
            logger.error("No errors, but data is empty.")
            return SafErrors.UNKNOWN.left()
        }

        span.setAttribute("client.outcome", "ok")
        return body.data.journalpost.right()
    }

    val JOURNALPOST_QUERY =
        $$"""
        query FindJournalpost($eksternReferanseId: String!) {
            journalpost(eksternReferanseId: $eksternReferanseId) {
                journalpostId
            }
        }
        """
            .trimIndent()

    private suspend fun getToken() =
        texasClient.entraIdToken(
            "teamdokumenthandtering",
            "saf",
            cluster = environment.runtime.cluster.toFssCluster(),
        )
}

private data class GrapghQLBody(val query: String, val variables: GraphQLVariables)

private data class GraphQLVariables(val eksternReferanseId: String)

private data class GraphQLResponse(val data: GraphQLJournalPost?, val errors: List<GraphQLError>?)

private data class GraphQLJournalPost(val journalpost: Journalpost? = null)

private data class GraphQLError(val message: String, val extensions: GraphQLExtensions)

private data class GraphQLExtensions(val code: String? = null, val classification: String? = null)
