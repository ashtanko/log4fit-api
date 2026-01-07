package dev.shtanko.model

data class ExposedToken(
    val id: Int? = 0,
    val token: String,
    val refreshToken: String,
    val userId: String,
    val revoked: Boolean = false
)

data class ExposedUser(
    val id: String,
    val name: String,
    val password: String,
    val email: String,
)
