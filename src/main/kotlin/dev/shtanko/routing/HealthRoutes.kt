package dev.shtanko.routing

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthCheck() {
    get("/health") {
        call.respondText("OK")
    }
}
