package no.nav.tsm.core.utils

import io.ktor.server.config.ApplicationConfig
import no.nav.tsm.ktor.auth.texas.TexasTarget
import no.nav.tsm.ktor.logger
import no.nav.tsm.ktor.nais.RuntimeCluster
import no.nav.tsm.ktor.nais.getRuntimeCluster

class RuntimeConfig(val cluster: RuntimeCluster)

class PostgresConfig(val jdbc: String, val username: String, val password: String)

class ExternalApi(val oppgaveApi: ExternalApiConfig, val safApi: ExternalApiConfig)

class ExternalApiConfig(val url: String)

class Environment(
    val runtime: RuntimeConfig,
    val postgres: PostgresConfig,
    val external: () -> ExternalApi,
)

fun initializeEnvironment(config: ApplicationConfig): Environment {
    return Environment(
        runtime = RuntimeConfig(cluster = getRuntimeCluster()),
        postgres =
            PostgresConfig(
                jdbc = config.property("postgres.jdbc").getString(),
                username = config.property("postgres.username").getString(),
                password = config.property("postgres.password").getString(),
            ),
        external = {
            ExternalApi(
                oppgaveApi =
                    ExternalApiConfig(url = config.property("external.oppgaveApi.url").getString()),
                safApi = ExternalApiConfig(url = config.property("external.safApi.url").getString()),
            )
        },
    )
}

fun RuntimeCluster.toFssCluster(): TexasTarget {
    val logger = logger()
    return when (this) {
        RuntimeCluster.DEV -> TexasTarget.DEV_FSS
        RuntimeCluster.PROD -> TexasTarget.PROD_FSS
        else -> {
            logger.error("Mapping unsupported TexasTarget from env. Target is $this")
            throw RuntimeException("Mapping unsupported TexasTarget from env. Target is $this")
        }
    }
}
