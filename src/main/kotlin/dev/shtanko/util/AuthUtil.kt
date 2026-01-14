package dev.shtanko.util

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.userId(): String? {
    return this.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
}
