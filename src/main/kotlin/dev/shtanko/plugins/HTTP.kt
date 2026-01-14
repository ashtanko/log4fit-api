package dev.shtanko.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

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
