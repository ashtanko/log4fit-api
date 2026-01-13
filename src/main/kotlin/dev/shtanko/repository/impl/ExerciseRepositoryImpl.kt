package dev.shtanko.repository.impl

import dev.shtanko.database.DatabaseFactory.query
import dev.shtanko.database.tables.ExerciseTable
import dev.shtanko.dto.response.ExerciseResponse
import dev.shtanko.dto.response.PagedResponse
import dev.shtanko.repository.ExerciseRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.selectAll

class ExerciseRepositoryImpl : ExerciseRepository {
    override suspend fun getAllExercises(limit: Int, offset: Long): PagedResponse<ExerciseResponse> = query {
        val total = ExerciseTable.selectAll().count()
        val exercises = ExerciseTable.selectAll()
            .limit(limit).offset(offset)
            .map { toExerciseResponse(it) }

        PagedResponse(
            data = exercises,
            page = (offset / limit).toInt() + 1,
            limit = limit,
            total = total
        )
    }

    override suspend fun getExerciseById(id: String): ExerciseResponse? = query {
        ExerciseTable.selectAll().where { ExerciseTable.id eq id }
            .map { toExerciseResponse(it) }
            .singleOrNull()
    }

    override suspend fun searchExercises(query: String, limit: Int, offset: Long): PagedResponse<ExerciseResponse> =
        query {
            val searchCondition = ExerciseTable.name like "%$query%"
            val total = ExerciseTable.selectAll().where { searchCondition }.count()
            val exercises = ExerciseTable.selectAll().where { searchCondition }
                .limit(limit).offset(offset)
                .map { toExerciseResponse(it) }

            PagedResponse(
                data = exercises,
                page = (offset / limit).toInt() + 1,
                limit = limit,
                total = total
            )
        }

    private fun toExerciseResponse(row: ResultRow): ExerciseResponse = ExerciseResponse(
        id = row[ExerciseTable.id].value,
        name = row[ExerciseTable.name],
        description = row[ExerciseTable.description],
        category = row[ExerciseTable.category],
        muscleGroup = row[ExerciseTable.muscleGroup],
        equipment = row[ExerciseTable.equipment]
    )
}
