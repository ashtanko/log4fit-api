package dev.shtanko.repository

import dev.shtanko.database.DatabaseFactory
import dev.shtanko.database.tables.UserTokenTable
import dev.shtanko.database.tables.base.currentUtc
import dev.shtanko.model.ExposedToken
import dev.shtanko.repository.impl.TokenRepositoryImpl

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenRepositoryTest {

    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setup() {
        DatabaseFactory.init(isProd = false, isDev = false, dbName = "token_repo_test")
        transaction {
            SchemaUtils.create(UserTokenTable)
        }
        tokenRepository = TokenRepositoryImpl()
    }

    @Test
    fun `test save and find by token`() = runBlocking {
        val token = ExposedToken(
            token = "access-123",
            refreshToken = "refresh-123",
            userId = "user-1"
        )

        val saved = tokenRepository.save(token)
        assertTrue(saved)

        val found = tokenRepository.findByToken("access-123")
        assertNotNull(found)
        assertEquals(token.token, found.token)
        assertEquals(token.refreshToken, found.refreshToken)
        assertEquals(token.userId, found.userId)
    }

    @Test
    fun `test find by refresh token`() = runBlocking {
        val token = ExposedToken(
            token = "access-456",
            refreshToken = "refresh-456",
            userId = "user-2"
        )
        tokenRepository.save(token)

        val found = tokenRepository.findByRefreshToken("refresh-456")
        assertNotNull(found)
        assertEquals("user-2", found.userId)
    }

    @Test
    fun `test find by user id`() = runBlocking {
        val token = ExposedToken(
            token = "access-789",
            refreshToken = "refresh-789",
            userId = "user-3"
        )
        tokenRepository.save(token)

        val found = tokenRepository.findByUserId("user-3")
        assertNotNull(found)
        assertEquals("access-789", found.token)
    }

    @Test
    fun `test revoke all tokens`() = runBlocking {
        val token = ExposedToken(
            token = "access-abc",
            refreshToken = "refresh-abc",
            userId = "user-4"
        )
        tokenRepository.save(token)

        val revoked = tokenRepository.revokedAllTokens("user-4", currentUtc())
        assertTrue(revoked)

        val found = tokenRepository.findByUserId("user-4")
        assertNotNull(found)
        assertTrue(found.revoked)
    }

    @Test
    fun `test logout`() = runBlocking {
        val token = ExposedToken(
            token = "access-logout",
            refreshToken = "refresh-logout",
            userId = "user-5"
        )
        tokenRepository.save(token)

        val loggedOut = tokenRepository.logout("user-5", currentUtc())
        assertTrue(loggedOut)

        val found = tokenRepository.findByUserId("user-5")
        assertNotNull(found)
        assertTrue(found.revoked)
    }

    @Test
    fun `test returns null when token not found`() = runBlocking {
        assertNull(tokenRepository.findByToken("nonexistent"))
        assertNull(tokenRepository.findByRefreshToken("nonexistent"))
        assertNull(tokenRepository.findByUserId("nonexistent"))
    }
}
