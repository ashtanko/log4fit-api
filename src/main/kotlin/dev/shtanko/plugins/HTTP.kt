package dev.shtanko.plugins

import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
//    routing {
//        cacheOutput(2.seconds) {
//            get("/short") {
//                call.respond(Random.nextInt().toString())
//            }
//        }
//        cacheOutput {
//            get("/default") {
//                call.respond(Random.nextInt().toString())
//            }
//        }
//    }
}
