package no.nav.tsm.utils

import io.mockk.mockk
import no.nav.tsm.core.*
import no.nav.tsm.ktor.nais.RuntimeCluster
import org.testcontainers.postgresql.PostgreSQLContainer

fun createIntegrationEnvironment(postgres: PostgreSQLContainer) =
    Environment(
        runtime = RuntimeConfig(cluster = RuntimeCluster.LOCAL, name = "tsm-oppgave-12344"),
        jobs = mockk(relaxed = true),
        postgres =
            PostgresConfig(
                jdbc = "jdbc:${postgres.jdbcUrl.removePrefix("jdbc:")}",
                username = postgres.username,
                password = postgres.password,
            ),
        external = {
            ExternalApi(
                oppgaveApi = ExternalApiConfig(url = "http://localhost"),
                safApi = ExternalApiConfig(url = "http://localhost"),
            )
        },
    )
