package dev.shtanko.repository

import dev.shtanko.model.ExposedUser

interface UserRepository {
    suspend fun addUser(exposedUser: ExposedUser): Boolean

    suspend fun emailExist(email: String): Boolean

    suspend fun findUserByEmail(email: String): ExposedUser?

    suspend fun findUserById(id: String): ExposedUser?
}
