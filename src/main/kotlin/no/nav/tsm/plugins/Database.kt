package no.nav.tsm.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import no.nav.tsm.core.Environment
import no.nav.tsm.core.db.getFlyway
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

fun Application.configureDatabase() {
    val env: Environment by dependencies

    val flyway = getFlyway(env.postgres)

    flyway.clean()
    flyway.migrate()

    R2dbcDatabase.connect(
        url = env.postgres.jdbc,
        user = env.postgres.username,
        password = env.postgres.password,
    )
}
