package dev.shtanko.routing

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthCheck() {
    get("/health") {
        call.respondText("OK")
    }
}
