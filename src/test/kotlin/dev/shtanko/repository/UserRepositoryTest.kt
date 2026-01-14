package dev.shtanko.repository

import dev.shtanko.database.DatabaseFactory
import dev.shtanko.database.tables.UserTable
import dev.shtanko.model.ExposedUser
import dev.shtanko.repository.impl.UserRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        DatabaseFactory.init(isProd = false, isDev = false, dbName = "user_repo_test")
        transaction {
            SchemaUtils.create(UserTable)
        }
        userRepository = UserRepositoryImpl()
    }

    @Test
    fun `test add user and find user by id`() = runBlocking {
        val user = ExposedUser(
            id = "user-1",
            name = "John Doe",
            email = "john@example.com",
            password = "password123"
        )

        val added = userRepository.addUser(user)
        assertTrue(added)

        val foundUser = userRepository.findUserById("user-1")
        assertNotNull(foundUser)
        assertEquals(user.id, foundUser.id)
        assertEquals(user.name, foundUser.name)
        assertEquals(user.email, foundUser.email)
    }

    @Test
    fun `test find user by email`() = runBlocking {
        val user = ExposedUser(
            id = "user-2",
            name = "Jane Doe",
            email = "jane@example.com",
            password = "password123"
        )
        userRepository.addUser(user)

        val foundUser = userRepository.findUserByEmail("jane@example.com")
        assertNotNull(foundUser)
        assertEquals("user-2", foundUser.id)
    }

    @Test
    fun `test email exists`() = runBlocking {
        val user = ExposedUser(
            id = "user-3",
            name = "Bob Smith",
            email = "bob@example.com",
            password = "password123"
        )
        userRepository.addUser(user)

        assertTrue(userRepository.emailExist("bob@example.com"))
        assertFalse(userRepository.emailExist("nonexistent@example.com"))
    }

    @Test
    fun `test find user returns null when not found`() = runBlocking {
        assertNull(userRepository.findUserById("nonexistent"))
        assertNull(userRepository.findUserByEmail("nonexistent@example.com"))
    }
}
