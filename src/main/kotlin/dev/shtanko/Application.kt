package dev.shtanko

import configureCORS
import dev.shtanko.database.DatabaseFactory
import dev.shtanko.plugins.configureAdministration
import dev.shtanko.plugins.configureAsyncApi
import dev.shtanko.plugins.configureExceptionHandler
import dev.shtanko.plugins.configureFirebase
import dev.shtanko.plugins.configureHTTP
import dev.shtanko.plugins.configureKoin
import dev.shtanko.plugins.configureMonitoring
import dev.shtanko.plugins.configureOpenApi
import dev.shtanko.plugins.configureRateLimit
import dev.shtanko.plugins.configureRouting
import dev.shtanko.plugins.configureSecurity
import dev.shtanko.plugins.configureSerialization
import dev.shtanko.plugins.configureValidation
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

fun main() {
    val env = System.getenv("ENV")
    val isProd = env == "production"
    val port = System.getenv("PORT")?.toInt() ?: if (isProd) 8083 else 8082
    val isDev = env == "dev"
    System.setProperty("io.ktor.development", if (isProd) "false" else "true")

    LoggerFactory.getLogger("Application")
        .info("Starting application. ENV=$env, isProd=$isProd, PORT=$port DB_PORT: ${System.getenv("DB_PORT")}")

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(isProd, isDev)
    }.start(wait = true)
}

fun Application.module(isProd: Boolean, isDev: Boolean) {
    DatabaseFactory.init(isProd, isDev)

    // Always run migrations unless specifically disabled (e.g. via a property)
    // For H2 in-memory, we always need to run them to create the schema.
    DatabaseFactory.runFlywayMigrations()

//    if (isProd or isDev) {
//        DatabaseFactory.runFlywayMigrations()
//    }

    configureCORS()
    configureFirebase()

    configureAdministration()
    configureSerialization()
    configureExceptionHandler()
    configureValidation()
    configureRateLimit()
    configureKoin()
    configureMonitoring()
    configureSecurity()
    configureHTTP()
    configureRouting()
    configureAsyncApi()
    configureOpenApi()
}
