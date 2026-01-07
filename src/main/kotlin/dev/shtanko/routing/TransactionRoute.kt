package dev.shtanko.routing

import dev.shtanko.service.JwtService.Companion.getUsername
import dev.shtanko.service.TransactionService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.transactionRoute(transactionService: TransactionService){
    get("/recent") {
        val username = call.getUsername() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val responsePayload =  transactionService.recentTransactions();
        call.respond(HttpStatusCode.OK,responsePayload)
    }
}
