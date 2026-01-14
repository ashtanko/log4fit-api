package dev.shtanko.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ActivityResponse(
    val id: String,
    val userId: String,
    val exerciseId: String,
    val exerciseName: String,
    val startTime: String,
    val endTime: String?,
    val notes: String?,
    val rpe: Int?,
    val status: String,
    val createdAt: String
)
