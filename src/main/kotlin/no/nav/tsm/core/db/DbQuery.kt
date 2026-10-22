package no.nav.tsm.core.db

import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

suspend fun <T> dbQuery(statement: suspend JdbcTransaction.() -> T): T = suspendTransaction {
    statement()
}
