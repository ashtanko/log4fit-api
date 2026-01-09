package dev.shtanko.routing

import dev.shtanko.dto.request.GoogleLoginRequest
import dev.shtanko.dto.request.LoginRequest
import dev.shtanko.dto.request.RegistrationRequest
import dev.shtanko.service.AuthService
import dev.shtanko.service.JwtService.Companion.getUsername
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.register(authService: AuthService) {
    post("/register") {
        val request = call.receive<RegistrationRequest>()
        val tokenResponse = authService.registerUser(request)
        call.respond(HttpStatusCode.Created, tokenResponse)
    }
}

fun Route.login(authService: AuthService) {
    post("/login") {
        val loginRequest = call.receive<LoginRequest>()
        val loginResponse = authService.login(loginRequest)
        call.respond(HttpStatusCode.OK, loginResponse)
    }
}

fun Route.googleLogin(authService: AuthService) {
    post("/google") {
        val request = call.receive<GoogleLoginRequest>()
        val tokenResponse = authService.loginWithGoogle(request)
        call.respond(HttpStatusCode.OK, tokenResponse)
    }
}

fun Route.authMe(authService: AuthService) {
    get("/me") {
        val username = call.getUsername() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val authMe = authService.authMe(username.trim())
        call.respond(HttpStatusCode.OK, authMe)
    }
}

fun Route.refreshToken(authService: AuthService) {
    post("/refresh") {
        val username = call.getUsername() ?: return@post call.respond(HttpStatusCode.BadRequest)
        val tokenResponse = authService.refreshToken(username.trim())
        call.respond(HttpStatusCode.OK, tokenResponse)
    }
}
