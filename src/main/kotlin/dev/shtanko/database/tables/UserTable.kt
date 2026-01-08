package dev.shtanko.database.tables

import dev.shtanko.database.tables.base.BaseIdTable

object UserTable : BaseIdTable("users") {
    val email = varchar("email", 100).uniqueIndex()
    val name = varchar("name", 100)
    val password = text("password")
    override val primaryKey = PrimaryKey(id, name = "users_pk")
}
