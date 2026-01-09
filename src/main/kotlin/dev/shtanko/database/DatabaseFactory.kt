package dev.shtanko.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import javax.sql.DataSource

object DatabaseFactory {

    private lateinit var dataSource: DataSource
    private val log = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init(isProd: Boolean, isDev: Boolean) {
        dataSource = when {
            isProd || isDev -> createPostgresDataSource()
            else -> createH2DataSource()
        }
        Database.connect(dataSource)
    }

    fun runFlywayMigrations() {
        val flyway = Flyway.configure().dataSource(dataSource).load()
        try {
            flyway.info()
            flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running flyway migration", e)
            throw e
        }
        log.info("Flyway migration has finished")
    }

    private fun createH2DataSource(): HikariDataSource {
        log.info("Using H2 in-memory database for local development.")
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
        log.info("Using PostgreSQL database for: ${System.getenv("DB_URL")} ${System.getenv("DB_USER")}")
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
