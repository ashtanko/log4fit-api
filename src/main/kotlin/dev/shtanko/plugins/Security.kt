package dev.shtanko.plugins

import dev.shtanko.service.JwtService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.request.header
import io.ktor.server.response.respond
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
