package no.nav.tsm.core.db

import no.nav.tsm.core.utils.PostgresConfig
import org.flywaydb.core.Flyway

fun getFlyway(postgresConfig: PostgresConfig): Flyway =
    Flyway.configure()
        .dataSource(postgresConfig.jdbc, postgresConfig.username, postgresConfig.password)
        .locations("db/migration")
        .cleanDisabled(false)
        .load()
