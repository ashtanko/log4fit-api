package dev.shtanko.repository

import dev.shtanko.model.ExposedToken
import kotlinx.datetime.LocalDateTime

interface TokenRepository {
    suspend fun save(exposedToken: ExposedToken): Boolean

    suspend fun findByToken(accessToken: String): ExposedToken?

    suspend fun findByRefreshToken(refreshToken: String): ExposedToken?

    suspend fun findByUserId(userId: String): ExposedToken?

    suspend fun revokedAllTokens(userId: String, dateTime: LocalDateTime): Boolean

    suspend fun logout(userId: String, dateTime: LocalDateTime): Boolean
}
