package dev.shtanko.plugins

import dev.shtanko.util.ConflictException
import dev.shtanko.util.NotFoundException
import dev.shtanko.util.ServerErrorException
import dev.shtanko.util.UnauthorizedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureExceptionHandler() {
    install(StatusPages) {
        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers["Retry-After"]
            call.respondText(text = "429: Too many requests. Wait for $retryAfter seconds.", status = status)
        }
        exception<Throwable> { call, cause ->
            when (cause) {
                is RequestValidationException -> {
                    call.respondText(cause.reasons.joinToString(","), status = HttpStatusCode.BadRequest)
                }

                is UnauthorizedException -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.Unauthorized)
                }

                is ConflictException -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.Conflict)
                }

                is NotFoundException -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.NotFound)
                }

                is ServerErrorException -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.InternalServerError)
                }

                else -> {
                    call.respondText(text = "${cause.message}", status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
