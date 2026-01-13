package dev.shtanko.database.tables

import dev.shtanko.database.tables.base.BaseIdTable
import org.jetbrains.exposed.sql.Column

object ExerciseTable : BaseIdTable("exercises") {
    val name: Column<String> = varchar("name", 255).uniqueIndex()
    val description: Column<String?> = text("description").nullable()
    val category: Column<String> = varchar("category", 50) // STRENGTH, CARDIO, CALISTHENICS
    val muscleGroup: Column<String> = varchar("muscle_group", 50) // CHEST, BACK, LEGS, ARMS, CORE, FULL_BODY
    val equipment: Column<String> = varchar("equipment", 50) // BODYWEIGHT, BARBELL, DUMBBELL, MACHINE
}
