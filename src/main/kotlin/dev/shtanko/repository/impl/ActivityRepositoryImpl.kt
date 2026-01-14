package dev.shtanko.repository.impl

import dev.shtanko.database.DatabaseFactory.query
import dev.shtanko.database.tables.ActivityTable
import dev.shtanko.database.tables.ExerciseTable
import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.dto.response.ActivityResponse
import dev.shtanko.dto.response.PagedResponse
import dev.shtanko.repository.ActivityRepository
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class ActivityRepositoryImpl : ActivityRepository {
    override suspend fun createActivity(userId: String, request: ActivityRequest): ActivityResponse? = query {
        val insertedId = ActivityTable.insertAndGetId {
            it[ActivityTable.userId] = userId
            it[ActivityTable.exerciseId] = request.exerciseId
            it[ActivityTable.startTime] = request.startTime.toLocalDateTime()
            it[ActivityTable.endTime] = request.endTime?.toLocalDateTime()
            it[ActivityTable.notes] = request.notes
            it[ActivityTable.rpe] = request.rpe
            it[ActivityTable.status] = request.status
        }

        findActivityById(insertedId.value)
    }

    override suspend fun getActivitiesByUserId(
        userId: String,
        limit: Int,
        offset: Long
    ): PagedResponse<ActivityResponse> =
        query {
            val total = ActivityTable.selectAll().where { ActivityTable.userId eq userId }.count()
            val activities = ActivityTable.join(
                ExerciseTable,
                org.jetbrains.exposed.sql.JoinType.INNER,
                ActivityTable.exerciseId,
                ExerciseTable.id
            ).selectAll().where { ActivityTable.userId eq userId }
                .limit(limit).offset(offset)
                .map { toActivityResponse(it) }

            PagedResponse(
                data = activities,
                page = (offset / limit).toInt() + 1,
                limit = limit,
                total = total
            )
        }

    override suspend fun getActivityById(id: String): ActivityResponse? = query {
        findActivityById(id)
    }

    private fun findActivityById(id: String): ActivityResponse? {
        return ActivityTable.join(
            ExerciseTable,
            org.jetbrains.exposed.sql.JoinType.INNER,
            ActivityTable.exerciseId,
            ExerciseTable.id
        ).selectAll().where { ActivityTable.id eq id }
            .map { toActivityResponse(it) }
            .singleOrNull()
    }

    private fun toActivityResponse(row: ResultRow): ActivityResponse = ActivityResponse(
        id = row[ActivityTable.id].value,
        userId = row[ActivityTable.userId].value,
        exerciseId = row[ActivityTable.exerciseId].value,
        exerciseName = row[ExerciseTable.name],
        startTime = row[ActivityTable.startTime].toString(),
        endTime = row[ActivityTable.endTime]?.toString(),
        notes = row[ActivityTable.notes],
        rpe = row[ActivityTable.rpe],
        status = row[ActivityTable.status],
        createdAt = row[ActivityTable.createdAt].toString()
    )
}
