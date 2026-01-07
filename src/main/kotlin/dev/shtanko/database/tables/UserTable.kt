package dev.shtanko.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : Table("users") {
    val id = varchar("id", length = 100)
    val email = varchar("email",100).uniqueIndex()
    val name = varchar("name",100)
    val password = text("password")
    val createdAt = datetime("server_time").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id, name = "users_pk")
}