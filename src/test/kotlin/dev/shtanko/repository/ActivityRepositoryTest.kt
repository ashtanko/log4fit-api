package dev.shtanko.repository

import dev.shtanko.database.DatabaseFactory
import dev.shtanko.database.tables.ActivityTable
import dev.shtanko.database.tables.ExerciseTable
import dev.shtanko.database.tables.UserTable
import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.repository.impl.ActivityRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ActivityRepositoryTest {

    private lateinit var activityRepository: ActivityRepository
    private val testUserId = "user-123"
    private val testExerciseId = "ex-456"

    @Before
    fun setup() {
        DatabaseFactory.init(isProd = false, isDev = false, dbName = "activity_repo_test")
        transaction {
            SchemaUtils.create(UserTable, ExerciseTable, ActivityTable)

            // Seed User
            UserTable.insert {
                it[id] = testUserId
                it[name] = "Test User"
                it[email] = "test@example.com"
                it[password] = "hash"
            }

            // Seed Exercise
            ExerciseTable.insert {
                it[id] = testExerciseId
                it[name] = "Bench Press"
                it[category] = "STRENGTH"
                it[muscleGroup] = "CHEST"
                it[equipment] = "BARBELL"
            }
        }
        activityRepository = ActivityRepositoryImpl()
    }

    @Test
    fun `test create activity and get by id`() = runBlocking {
        val request = ActivityRequest(
            exerciseId = testExerciseId,
            startTime = "2023-10-27T10:00:00",
            notes = "Feeling strong",
            rpe = 8
        )

        val created = activityRepository.createActivity(testUserId, request)
        assertNotNull(created)
        assertEquals(testUserId, created.userId)
        assertEquals(testExerciseId, created.exerciseId)
        assertEquals("Bench Press", created.exerciseName)
        assertEquals(8, created.rpe)

        val found = activityRepository.getActivityById(created.id)
        assertNotNull(found)
        assertEquals(created.id, found.id)
    }

    @Test
    fun `test get activities by user id`() = runBlocking {
        val request = ActivityRequest(
            exerciseId = testExerciseId,
            startTime = "2023-10-27T10:00:00"
        )
        activityRepository.createActivity(testUserId, request)
        activityRepository.createActivity(testUserId, request.copy(startTime = "2023-10-28T10:00:00"))

        val result = activityRepository.getActivitiesByUserId(testUserId, limit = 10, offset = 0)
        assertEquals(2, result.data.size)
        assertEquals(2, result.total)
    }

    @Test
    fun `test get activity returns null when not found`() = runBlocking {
        assertNull(activityRepository.getActivityById("nonexistent-id"))
    }

    @Test
    fun `test activities for different users are isolated`() = runBlocking {
        val otherUserId = "user-999"
        transaction {
            UserTable.insert {
                it[id] = otherUserId
                it[name] = "Other User"
                it[email] = "other@example.com"
                it[password] = "hash"
            }
        }

        activityRepository.createActivity(testUserId, ActivityRequest(testExerciseId, "2023-10-27T10:00:00"))
        
        val user1Activities = activityRepository.getActivitiesByUserId(testUserId, 10, 0)
        val user2Activities = activityRepository.getActivitiesByUserId(otherUserId, 10, 0)

        assertEquals(1, user1Activities.total)
        assertEquals(0, user2Activities.total)
    }
}
