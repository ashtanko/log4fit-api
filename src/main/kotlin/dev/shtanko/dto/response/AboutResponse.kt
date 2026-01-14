package dev.shtanko.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AboutResponse(
    val name: String,
    val email: String,
    val sessionId: String
)
