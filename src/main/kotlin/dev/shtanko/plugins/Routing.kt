package dev.shtanko.plugins

import dev.shtanko.routing.authMe
import dev.shtanko.routing.googleLogin
import dev.shtanko.routing.healthCheck
import dev.shtanko.routing.login
import dev.shtanko.routing.refreshToken
import dev.shtanko.routing.register
import dev.shtanko.routing.transactionRoute
import dev.shtanko.service.AuthService
import dev.shtanko.service.TransactionService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val transactionService by inject<TransactionService>()
    routing {
        healthCheck()
        route("/api") {
            rateLimit(RateLimitName("auth")) {
                route("/auth") {
                    register(authService)
                    login(authService)
                    googleLogin(authService)
                }
            }

            rateLimit(RateLimitName("public")) {
                get("/public-api") {
                    val requestsLeft = call.response.headers["X-RateLimit-Remaining"]
                    call.respondText("Welcome to public API! $requestsLeft requests left.")
                }
            }
            rateLimit(RateLimitName("protected")) {
                get("/protected-api") {
                    val requestsLeft = call.response.headers["X-RateLimit-Remaining"]
                    val login = call.request.queryParameters["login"]
                    call.respondText("Welcome to protected API, $login! $requestsLeft requests left.")
                }
            }

            rateLimit(RateLimitName("auth")) {
                route("/auth") {
                    authenticate("access-token-auth") {
                        authMe(authService = authService)
                    }

                    authenticate("refresh-token-auth") {
                        refreshToken(authService = authService)
                    }
                }
            }

            route("/transactions") {
                authenticate("access-token-auth") {
                    transactionRoute(transactionService)
                }
            }
        }
    }
}
