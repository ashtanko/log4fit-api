package dev.shtanko.repository

import dev.shtanko.database.DatabaseFactory
import dev.shtanko.database.tables.ExerciseTable
import dev.shtanko.repository.impl.ExerciseRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExerciseRepositoryTest {

    private lateinit var exerciseRepository: ExerciseRepository

    @Before
    fun setup() {
        // Use a unique database name for this test class
        DatabaseFactory.init(isProd = false, isDev = false, dbName = "exercise_repo_test")
        transaction {
            SchemaUtils.create(ExerciseTable)
            
            // Insert some test data
            ExerciseTable.insert {
                it[id] = "ex-1"
                it[name] = "Squat"
                it[description] = "A basic lower body exercise"
                it[category] = "STRENGTH"
                it[muscleGroup] = "LEGS"
                it[equipment] = "BARBELL"
            }
            
            ExerciseTable.insert {
                it[id] = "ex-2"
                it[name] = "Push-up"
                it[description] = "A basic upper body exercise"
                it[category] = "CALISTHENICS"
                it[muscleGroup] = "CHEST"
                it[equipment] = "BODYWEIGHT"
            }
        }
        exerciseRepository = ExerciseRepositoryImpl()
    }

    @Test
    fun `test get all exercises`() = runBlocking {
        val result = exerciseRepository.getAllExercises(limit = 10, offset = 0)
        assertEquals(2, result.data.size)
        assertEquals(2, result.total)
    }

    @Test
    fun `test get exercise by id`() = runBlocking {
        val exercise = exerciseRepository.getExerciseById("ex-1")
        assertNotNull(exercise)
        assertEquals("Squat", exercise.name)
    }

    @Test
    fun `test get exercise by id returns null when not found`() = runBlocking {
        val exercise = exerciseRepository.getExerciseById("nonexistent")
        assertNull(exercise)
    }

    @Test
    fun `test search exercises`() = runBlocking {
        val result = exerciseRepository.searchExercises(query = "Push", limit = 10, offset = 0)
        assertEquals(1, result.data.size)
        assertEquals("Push-up", result.data[0].name)
    }

    @Test
    fun `test search exercises with no results`() = runBlocking {
        val result = exerciseRepository.searchExercises(query = "Deadlift", limit = 10, offset = 0)
        assertEquals(0, result.data.size)
        assertEquals(0, result.total)
    }

    @Test
    fun `test pagination`() = runBlocking {
        val result = exerciseRepository.getAllExercises(limit = 1, offset = 0)
        assertEquals(1, result.data.size)
        assertEquals(2, result.total)
        assertEquals(1, result.page)
        
        val result2 = exerciseRepository.getAllExercises(limit = 1, offset = 1)
        assertEquals(1, result2.data.size)
        assertEquals(2, result2.page)
    }
}
