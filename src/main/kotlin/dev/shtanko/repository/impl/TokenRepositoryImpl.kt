package dev.shtanko.repository.impl

import dev.shtanko.database.TableConverter
import dev.shtanko.database.tables.UserTokenTable
import dev.shtanko.database.DatabaseFactory
import dev.shtanko.model.ExposedToken
import dev.shtanko.repository.TokenRepository
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class TokenRepositoryImpl : TokenRepository {
    override suspend fun save(exposedToken: ExposedToken) = DatabaseFactory.query {
        val result = UserTokenTable.insert {
            it[token] = exposedToken.token
            it[refreshToken] = exposedToken.refreshToken
            it[userId] = exposedToken.userId
        }
        result.insertedCount > 0
    }


    override suspend fun findByToken(accessToken: String): ExposedToken? = DatabaseFactory.query {
        UserTokenTable.selectAll()
            .where(UserTokenTable.token eq accessToken)
            .map(TableConverter::rowToExposedToken).singleOrNull()
    }


    override suspend fun findByRefreshToken(refreshToken: String): ExposedToken? = DatabaseFactory.query {
        UserTokenTable.selectAll()
            .where(UserTokenTable.refreshToken eq refreshToken)
            .map(TableConverter::rowToExposedToken).singleOrNull()
    }


    override suspend fun findByUserId(userId: String) = DatabaseFactory.query {
        UserTokenTable.selectAll()
            .where(UserTokenTable.userId eq userId).singleOrNull()?.let { TableConverter.rowToExposedToken(it) }
    }


    override suspend fun revokedAllTokens(userId: String, dateTime: LocalDateTime) = DatabaseFactory.query {
        UserTokenTable.update({ UserTokenTable.userId eq userId }) {
            it[revoked] = true
            it[revokedAt] = dateTime
        } > 0
    }


    override suspend fun logout(userId: String, dateTime: LocalDateTime) =
        DatabaseFactory.query {
            UserTokenTable.update({ UserTokenTable.userId eq userId }) {
                it[revokedAt] = dateTime
                it[logoutAt] = dateTime
                it[revoked] = true
                it[logout] = true
            } > 0
        }
}