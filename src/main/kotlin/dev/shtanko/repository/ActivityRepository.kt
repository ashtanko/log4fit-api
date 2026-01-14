package dev.shtanko.repository

import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.dto.response.ActivityResponse
import dev.shtanko.dto.response.PagedResponse

interface ActivityRepository {
    suspend fun createActivity(userId: String, request: ActivityRequest): ActivityResponse?
    suspend fun getActivitiesByUserId(userId: String, limit: Int, offset: Long): PagedResponse<ActivityResponse>
    suspend fun getActivityById(id: String): ActivityResponse?
}
