package no.nav.tsm.utils

import no.nav.tsm.core.db.migrate
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.postgresql.PostgreSQLContainer

abstract class WithPostgresql protected constructor() {
    companion object {
        val postgres = PostgreSQLContainer("postgres:17-alpine").apply { start() }
        val config = createIntegrationEnvironment(postgres)

        fun runMigrations(clean: Boolean = false) {
            migrate(config.postgres.jdbc, clean)
        }

        fun connect() {
            Database.connect(url = config.postgres.jdbc)
        }
    }
}
