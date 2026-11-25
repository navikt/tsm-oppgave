package no.nav.tsm.core.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

private const val CONNECT_RETRIES = 5

fun migrate(jdbcUrl: String, clean: Boolean): MigrateResult {
    val flyway =
        Flyway.configure()
            .dataSource(jdbcUrl, null, null)
            .connectRetries(CONNECT_RETRIES)
            .locations("db/migration")
            .cleanDisabled(false)
            .load()

    if (clean) {
        flyway.clean()
    }
    return flyway.migrate()
}
