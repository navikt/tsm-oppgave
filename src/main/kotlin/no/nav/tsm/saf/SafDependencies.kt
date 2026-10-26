package no.nav.tsm.saf

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.nav.tsm.ktor.di.dynamicDependencies
import no.nav.tsm.saf.client.SafCloudClient
import no.nav.tsm.saf.client.SafLocalClient

fun Application.configureSafDependencies() {
    dynamicDependencies {
        cloud { provide(SafCloudClient::class) }
        local { provide(SafLocalClient::class) }
    }
    dependencies { provide(SafService::class) }
}
