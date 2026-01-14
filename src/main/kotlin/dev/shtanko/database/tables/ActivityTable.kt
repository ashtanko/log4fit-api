package dev.shtanko.database.tables

import dev.shtanko.database.tables.base.BaseIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ActivityTable : BaseIdTable("activities") {
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val exerciseId = reference("exercise_id", ExerciseTable.id, onDelete = ReferenceOption.CASCADE)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time").nullable()
    val notes = text("notes").nullable()
    val rpe = integer("rpe").nullable() // Rate of Perceived Exertion (1-10)
    val status = varchar("status", 20).default("COMPLETED") // IN_PROGRESS, COMPLETED, CANCELLED
}
