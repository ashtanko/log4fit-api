package dev.shtanko.repository.impl

import dev.shtanko.database.DatabaseFactory
import dev.shtanko.database.TableConverter
import dev.shtanko.database.tables.UserTable
import dev.shtanko.model.ExposedUser
import dev.shtanko.repository.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserRepositoryImpl : UserRepository {
    override suspend fun addUser(exposedUser: ExposedUser) = DatabaseFactory.query {
        UserTable.insert {
            it[id] = exposedUser.id
            it[name] = exposedUser.name
            it[email] = exposedUser.email
            it[password] = exposedUser.password
        }
    }.insertedCount > 0

    override suspend fun emailExist(email: String) = DatabaseFactory.query {
        UserTable.selectAll().where(UserTable.email eq (email))
            .count() > 0
    }

    override suspend fun findUserByEmail(email: String): ExposedUser? =
        DatabaseFactory.query {
            UserTable.selectAll()
                .where(UserTable.email eq email)
                .map(TableConverter::rowToExposedUser)
                .singleOrNull()
        }

    override suspend fun findUserById(id: String): ExposedUser? = DatabaseFactory.query {
        UserTable.selectAll()
            .where(UserTable.id eq id)
            .map(TableConverter::rowToExposedUser)
            .singleOrNull()
    }
}
