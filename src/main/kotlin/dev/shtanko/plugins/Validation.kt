package dev.shtanko.plugins

import dev.shtanko.dto.request.LoginRequest
import dev.shtanko.dto.request.RegistrationRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<LoginRequest> { payload ->
            when{
                payload.email.isBlank() -> ValidationResult.Invalid("Email can't be empty!")
                payload.password.isBlank() -> ValidationResult.Invalid("Password can't be empty!")
                else -> ValidationResult.Valid
            }
        }

        validate<RegistrationRequest> { payload ->
            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

            when{
                payload.name.isBlank() -> ValidationResult.Invalid("Name can't be empty!")
                payload.password.isBlank() -> ValidationResult.Invalid("Password can't be empty!")
                !payload.password.matches(passwordRegex) -> ValidationResult.Invalid("Password must be at least 8 characters long and contain at least one letter and one number")
                payload.email.isBlank() -> ValidationResult.Invalid("Email can't be empty!")
                !payload.email.matches(emailRegex) -> ValidationResult.Invalid("Invalid email format")
                else -> ValidationResult.Valid
            }
        }
    }

}
