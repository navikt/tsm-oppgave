package no.nav.tsm.core.utils

import io.ktor.server.config.ApplicationConfig

class ExternalApi(
    val oppgaveApi: String
)

class Environment (
    val external: () -> ExternalApi,
)

fun initializeEnvironment(config: ApplicationConfig): Environment {
    return Environment(
        external = {
            ExternalApi(
                oppgaveApi = config.property("external.oppgaveApi").getString()
            )
        }
    )
}