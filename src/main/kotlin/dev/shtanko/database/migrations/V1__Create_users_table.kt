package dev.shtanko.database.migrations

import dev.shtanko.database.tables.UserTable
import dev.shtanko.database.tables.UserTokenTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class V1__Create_users_table : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(UserTable, UserTokenTable)
        }
    }
}
