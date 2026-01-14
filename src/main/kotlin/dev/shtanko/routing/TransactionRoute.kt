package dev.shtanko.routing

import dev.shtanko.service.JwtService.Companion.getUsername
import dev.shtanko.service.TransactionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.transactionRoute(transactionService: TransactionService) {
    get("/recent") {
        call.getUsername() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val responsePayload = transactionService.recentTransactions()
        call.respond(HttpStatusCode.OK, responsePayload)
    }
}
