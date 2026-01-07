package dev.shtanko.database

import dev.shtanko.database.tables.UserTable
import dev.shtanko.database.tables.UserTokenTable
import dev.shtanko.model.ExposedToken
import dev.shtanko.model.ExposedUser
import org.jetbrains.exposed.sql.ResultRow

object TableConverter {

    fun rowToExposedUser(row: ResultRow): ExposedUser {
        return ExposedUser(
            id = row[UserTable.id],
            name = row[UserTable.name],
            password = row[UserTable.password],
            email = row[UserTable.email],
        )
    }

    fun rowToExposedToken(row: ResultRow): ExposedToken {
        return ExposedToken(
            id = row[UserTokenTable.id],
            token = row[UserTokenTable.token],
            refreshToken = row[UserTokenTable.refreshToken],
            userId = row[UserTokenTable.userId],
            revoked = row[UserTokenTable.revoked]
        )
    }
}
