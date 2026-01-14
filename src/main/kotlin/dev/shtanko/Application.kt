package dev.shtanko

import configureCORS
import dev.shtanko.database.DatabaseFactory
import dev.shtanko.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

fun main() {
    val env = System.getenv("ENV")
    val isProd = env == "production"
    val port = System.getenv("PORT")?.toInt() ?: if (isProd) 8083 else 8082
    val isDev = env == "dev"
    System.setProperty("io.ktor.development", if (isProd) "false" else "true")

    LoggerFactory.getLogger("Application").info("Starting application. ENV=$env, isProd=$isProd, PORT=$port DB_PORT: ${System.getenv("DB_PORT")}")

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
