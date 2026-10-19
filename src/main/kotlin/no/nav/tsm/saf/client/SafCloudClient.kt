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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson3.jackson
import io.ktor.server.plugins.di.annotations.Named
import io.opentelemetry.api.trace.Span
import no.nav.tsm.core.utils.Environment
import no.nav.tsm.ktor.auth.texas.Texas
import no.nav.tsm.ktor.logger
import no.nav.tsm.saf.client.model.Journalpost
import no.nav.tsm.saf.client.model.SafErrors

class SafCloudClient(
    @Named("RetryHttpClient") httpClient: HttpClient,
    private val texasClient: Texas,
    private val environment: Environment,
): SafClient {
    private val logger = logger()

    private val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) { jackson {} }
    }

    override suspend fun getJournalpost(eksternReferanseId: String): Either<SafErrors, List<Journalpost>> {
        val span = Span.current()
        val (accessToken) = this.getToken()

        val query = GrapghQLBody(
            query = JOURNALPOST_QUERY,
            variables = GraphQLVariables(
                eksternReferanseId = eksternReferanseId
            )
        )
        logger.info("Debug query: $query")
        val response = httpClient.post(environment.external().safApi.url) {
            header("Authorization", "Bearer $accessToken")
            setBody(query)
            contentType(ContentType.Application.Json)
        }
        logger.info("Respnse body ${response.bodyAsText()}")
        val body = response.body<GraphQLResponse>()


        if (body.errors?.isNotEmpty() == true) {
            body.errors.forEach {
                return when {
                    it.extensions.code == "forbidden" -> {
                        span.setAttribute("client.outcome", "forbidden")
                        SafErrors.FORBIDDEN.left()
                    }

                    else -> {
                        span.setAttribute("client.outcome", "unknwn")
                        logger.error("saf responded with unhandled error: ${it.message} and code ${it.extensions.code}")
                        SafErrors.UNKNOWN.left()
                    }
                }
            }
        }

        if (body.data == null) {
            logger.error("No errors, but data is empty.")
            return SafErrors.UNKNOWN.left()
        }

        span.setAttribute("client.outcome", "ok")
        return body.data.right()
    }

    val JOURNALPOST_QUERY = $$"""
        query FindJournalpost($eksternReferanseId: String!) {
            journalpost(eksternReferanseId: $eksternReferanseId) {
                journalpostId
            }
        }
    """.trimIndent()

    private suspend fun getToken() = texasClient.entraIdToken("teamdokumenthandtering", "saf", cluster = environment.external().safApi.texasTargetCluster)
}

private data class GrapghQLBody(
    val query: String,
    val variables: GraphQLVariables
)

private data class GraphQLVariables(
    val eksternReferanseId: String,
)

private data class GraphQLResponse(
    val data: List<Journalpost>?,
    val errors: List<GraphQLError>?
)

private data class GraphQLError(
    val message: String,
    val extensions: GraphQLExtensions
)

private data class GraphQLExtensions(
    val code: String? = null,
    val classification: String? = null
)