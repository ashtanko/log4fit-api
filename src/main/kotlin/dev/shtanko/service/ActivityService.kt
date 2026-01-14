package dev.shtanko.service

import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.dto.response.ActivityResponse
import dev.shtanko.dto.response.PagedResponse
import dev.shtanko.repository.ActivityRepository

class ActivityService(private val activityRepository: ActivityRepository) {
    suspend fun createActivity(userId: String, request: ActivityRequest): ActivityResponse? =
        activityRepository.createActivity(userId, request)

    suspend fun getActivitiesByUserId(userId: String, page: Int, limit: Int): PagedResponse<ActivityResponse> {
        val offset = ((page - 1) * limit).toLong()
        return activityRepository.getActivitiesByUserId(userId, limit, offset)
    }

    suspend fun getActivityById(id: String): ActivityResponse? = activityRepository.getActivityById(id)
}
