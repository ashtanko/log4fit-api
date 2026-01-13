package dev.shtanko.repository

import dev.shtanko.dto.response.ExerciseResponse
import dev.shtanko.dto.response.PagedResponse

interface ExerciseRepository {
    suspend fun getAllExercises(limit: Int, offset: Long): PagedResponse<ExerciseResponse>
    suspend fun getExerciseById(id: String): ExerciseResponse?
    suspend fun searchExercises(query: String, limit: Int, offset: Long): PagedResponse<ExerciseResponse>
}
