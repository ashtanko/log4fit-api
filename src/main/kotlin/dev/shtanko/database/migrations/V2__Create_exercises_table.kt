package dev.shtanko.database.migrations

import dev.shtanko.database.tables.ExerciseTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class V2__Create_exercises_table : BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(ExerciseTable)

            // Calisthenics / Bodyweight
            insertExercise("Pull Up", "A compound upper-body exercise.", "CALISTHENICS", "BACK", "BODYWEIGHT")
            insertExercise("Push Up", "A classic upper-body pushing exercise.", "CALISTHENICS", "CHEST", "BODYWEIGHT")
            insertExercise("Dip", "Triceps and chest builder.", "CALISTHENICS", "ARMS", "BODYWEIGHT")
            insertExercise("Chin Up", "Biceps and back focus.", "CALISTHENICS", "BACK", "BODYWEIGHT")
            insertExercise("Muscle Up", "Advanced explosive pull and push.", "CALISTHENICS", "FULL_BODY", "BODYWEIGHT")
            insertExercise("L-Sit", "Core isometric hold.", "CALISTHENICS", "CORE", "BODYWEIGHT")
            insertExercise("Plank", "Core stability.", "CALISTHENICS", "CORE", "BODYWEIGHT")
            insertExercise("Squat (Bodyweight)", "Fundamental leg movement.", "CALISTHENICS", "LEGS", "BODYWEIGHT")
            insertExercise("Lunge", "Unilateral leg exercise.", "CALISTHENICS", "LEGS", "BODYWEIGHT")
            insertExercise("Burpee", "Full body conditioning.", "CALISTHENICS", "FULL_BODY", "BODYWEIGHT")

            // Strength / Gym
            insertExercise("Bench Press", "Compound chest press.", "STRENGTH", "CHEST", "BARBELL")
            insertExercise("Squat (Barbell)", "King of leg exercises.", "STRENGTH", "LEGS", "BARBELL")
            insertExercise("Deadlift", "Full body posterior chain.", "STRENGTH", "BACK", "BARBELL")
            insertExercise("Overhead Press", "Shoulder strength.", "STRENGTH", "SHOULDERS", "BARBELL")
            insertExercise("Barbell Row", "Back thickness.", "STRENGTH", "BACK", "BARBELL")
            insertExercise("Dumbbell Curl", "Bicep isolation.", "STRENGTH", "ARMS", "DUMBBELL")
            insertExercise("Tricep Extension", "Tricep isolation.", "STRENGTH", "ARMS", "DUMBBELL")

            // Cardio
            insertExercise("Running", "Outdoor or treadmill running.", "CARDIO", "LEGS", "NONE")
            insertExercise("Cycling", "Stationary or outdoor cycling.", "CARDIO", "LEGS", "BIKE")
            insertExercise("Rowing", "Full body cardio.", "CARDIO", "FULL_BODY", "MACHINE")
            insertExercise("Jump Rope", "High intensity cardio.", "CARDIO", "LEGS", "ROPE")
        }
    }

    private fun insertExercise(
        name: String,
        description: String,
        category: String,
        muscleGroup: String,
        equipment: String
    ) {
        ExerciseTable.insert {
            it[this.id] = name // Set ID to name for easier reference
            it[this.name] = name
            it[this.description] = description
            it[this.category] = category
            it[this.muscleGroup] = muscleGroup
            it[this.equipment] = equipment
        }
    }
}
