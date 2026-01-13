package dev.shtanko.service

import dev.shtanko.dto.response.ExerciseResponse
import dev.shtanko.dto.response.PagedResponse
import dev.shtanko.repository.ExerciseRepository

class ExerciseService(private val exerciseRepository: ExerciseRepository) {
    suspend fun getAllExercises(page: Int, limit: Int): PagedResponse<ExerciseResponse> {
        val offset = ((page - 1) * limit).toLong()
        return exerciseRepository.getAllExercises(limit, offset)
    }

    suspend fun getExerciseById(id: String): ExerciseResponse? = exerciseRepository.getExerciseById(id)

    suspend fun searchExercises(query: String, page: Int, limit: Int): PagedResponse<ExerciseResponse> {
        val offset = ((page - 1) * limit).toLong()
        return exerciseRepository.searchExercises(query, limit, offset)
    }
}
