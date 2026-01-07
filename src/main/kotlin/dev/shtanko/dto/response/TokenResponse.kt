package dev.shtanko.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
