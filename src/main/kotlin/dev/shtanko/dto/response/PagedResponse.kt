package dev.shtanko.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val data: List<T>,
    val page: Int,
    val limit: Int,
    val total: Long
)
