package no.nav.tsm.core.utils

import io.ktor.server.config.ApplicationConfig
import no.nav.tsm.ktor.auth.texas.TexasTarget
import no.nav.tsm.ktor.logger

class ExternalApi(
    val oppgaveApi: ExternalApiConfig
)

class ExternalApiConfig(
    val url: String,
    val texasTargetCluster: TexasTarget? = null
)

class Environment (
    val external: () -> ExternalApi,
)

fun initializeEnvironment(config: ApplicationConfig): Environment {
    return Environment(
        external = {
            ExternalApi(
                oppgaveApi = ExternalApiConfig(
                    url = config.property("external.oppgaveApi.url").getString(),
                    texasTargetCluster = getTexasTarget(config.property("external.oppgaveApi.texasTargetCluster").getString())
                )
            )
        }
    )
}

private fun getTexasTarget(target: String?): TexasTarget? {
    val logger = logger()
    return when (target) {
        "dev-gcp" -> TexasTarget.DEV
        "prod-gcp" -> TexasTarget.PROD
        "dev-fss" -> TexasTarget.DEV_FSS
        "prod-fss" -> TexasTarget.PROD_FSS
        null -> null
        else -> {
            logger.error("Mapping unsupported TexasTarget from env. Target is $target")
            throw RuntimeException("Mapping unsupported TexasTarget from env. Target is $target")
        }
    }
}