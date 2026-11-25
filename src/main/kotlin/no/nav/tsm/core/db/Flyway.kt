package no.nav.tsm.core.db

import no.nav.tsm.core.PostgresConfig
import org.flywaydb.core.Flyway

private const val CONNECT_RETRIES = 5

fun getFlyway(postgresConfig: PostgresConfig): Flyway =
    Flyway.configure()
        .dataSource(postgresConfig.jdbc, null, null)
        .connectRetries(CONNECT_RETRIES)
        .locations("db/migration")
        .cleanDisabled(false)
        .load()
