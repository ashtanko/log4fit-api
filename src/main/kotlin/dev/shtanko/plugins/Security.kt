package dev.shtanko.plugins

import dev.shtanko.service.JwtService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtService by inject<JwtService>()

    authentication {
        jwt("access-token-auth") {
            realm = jwtService.jwtRealm
            verifier(
                jwtService.jwtVerifier
            )
            validate { credential ->
                val authBearer = request.header("Authorization")
                jwtService.validate(authBearer, credential)
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    message = "Invalid or expired token"
                )
            }
        }

        jwt("refresh-token-auth") {
            realm = jwtService.jwtRealm
            verifier(
                jwtService.jwtVerifier
            )
            validate { credential ->
                val authBearer = request.header("Authorization")
                jwtService.validate(authBearer, credential, isAccessToken = false)
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Forbidden,
                    message = "Suspicious Token: Token is not valid or has been revoked"
                )
            }
        }

    }

}
