package no.nav.tsm.utils

import no.nav.tsm.core.utils.Environment
import no.nav.tsm.core.utils.ExternalApi
import no.nav.tsm.core.utils.ExternalApiConfig
import no.nav.tsm.core.utils.PostgresConfig
import no.nav.tsm.core.utils.RuntimeConfig
import no.nav.tsm.ktor.nais.RuntimeCluster
import org.testcontainers.postgresql.PostgreSQLContainer

fun createIntegrationEnvironment(postgres: PostgreSQLContainer) =
    Environment(
        runtime = RuntimeConfig(cluster = RuntimeCluster.LOCAL),
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
