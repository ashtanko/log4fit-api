package dev.shtanko.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.shtanko.database.tables.UserTable
import dev.shtanko.database.tables.UserTokenTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {

    fun init(isProd: Boolean, isDev: Boolean) {
        val database = if (isProd) {
            Database.connect(createPostgresDataSource())
        } else if(isDev) {
            Database.connect(createPostgresDataSource())
        } else {
            Database.connect(createH2DataSource())
        }
        transaction(database) {
            SchemaUtils.create(UserTable, UserTokenTable)
        }
    }

    private fun createH2DataSource(): HikariDataSource {
        LoggerFactory.getLogger(DatabaseFactory::class.java).info("Using H2 in-memory database for local development.")
        return HikariDataSource(HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

    private fun createPostgresDataSource(): HikariDataSource {
        LoggerFactory.getLogger(DatabaseFactory::class.java).info("Using PostgreSQL database for: ${System.getenv("DB_URL")} ${
            System.getenv(
                "DB_USER"
            )
        }")
        return HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("DB_URL")
            username = System.getenv("DB_USER")
            password = System.getenv("DB_PASSWORD")
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

    suspend fun <T> query(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
