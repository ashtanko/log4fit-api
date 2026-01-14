package dev.shtanko.database.tables.base

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.toEntity
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.util.UUID

abstract class BaseIdTable(name: String) : IdTable<String>(name) {
    override val id: Column<EntityID<String>> =
        varchar("id", 50).clientDefault { UUID.randomUUID().toString() }.uniqueIndex().entityId()
    val createdAt = datetime("created_at").clientDefault { currentUtc() }
    val updatedAt = datetime("updated_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

abstract class BaseEntity(id: EntityID<String>, table: BaseIdTable) : Entity<String>(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseEntityClass<E : BaseEntity>(table: BaseIdTable, entityType: Class<E>) :
    EntityClass<String, E>(table, entityType) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}

fun currentUtc(): kotlinx.datetime.LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
