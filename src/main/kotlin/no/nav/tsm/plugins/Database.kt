package no.nav.tsm.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.nav.tsm.core.db.getFlyway
import no.nav.tsm.core.utils.Environment
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabase() {
    val env: Environment by dependencies

    val flyway = getFlyway(env.postgres)

    flyway.clean()
    flyway.migrate()

    Database.connect(
        url = env.postgres.jdbc,
        user = env.postgres.username,
        password = env.postgres.password,
    )
}
