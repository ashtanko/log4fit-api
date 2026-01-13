package dev.shtanko.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseResponse(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val muscleGroup: String,
    val equipment: String
)
