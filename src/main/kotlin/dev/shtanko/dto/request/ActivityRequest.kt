package dev.shtanko.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ActivityRequest(
    val exerciseId: String,
    val startTime: String, // ISO-8601 format
    val endTime: String? = null,
    val notes: String? = null,
    val rpe: Int? = null,
    val status: String = "COMPLETED"
)
