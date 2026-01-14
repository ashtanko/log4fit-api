package dev.shtanko.routing

import dev.shtanko.dto.request.ActivityRequest
import dev.shtanko.dto.request.RegistrationRequest
import dev.shtanko.dto.response.ActivityResponse
import dev.shtanko.dto.response.PagedResponse
import dev.shtanko.dto.response.TokenResponse
import dev.shtanko.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ActivityRoutesTest {

    @Test
    fun `test full activity logging flow`() = testApplication {
        application {
            // Using a unique DB name for integration tests
            module(isProd = false, isDev = false)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 1. Register & Login to get token
        val authResponse = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest("Test Runner", "runner@test.com", "Password123"))
        }.body<TokenResponse>()
        
        val token = authResponse.accessToken

        // 2. Log an activity (Exercise ID 'Pull Up' comes from V2 migration)
        val activityReq = ActivityRequest(
            exerciseId = "Pull Up", 
            startTime = "2023-10-27T10:00:00",
            notes = "First integrated log",
            rpe = 7
        )

        val postResponse = client.post("/api/activities") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(activityReq)
        }
        
        assertEquals(HttpStatusCode.Created, postResponse.status)
        val createdActivity = postResponse.body<ActivityResponse>()
        assertEquals("Pull Up", createdActivity.exerciseName)

        // 3. Get activity history
        val getResponse = client.get("/api/activities") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val history = getResponse.body<PagedResponse<ActivityResponse>>()
        assertEquals(1, history.total)
        assertEquals(createdActivity.id, history.data[0].id)

        // 4. Get specific activity by ID
        val detailResponse = client.get("/api/activities/${createdActivity.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, detailResponse.status)
        assertEquals("Pull Up", detailResponse.body<ActivityResponse>().exerciseName)
    }

    @Test
    fun `test unauthorized access to activities`() = testApplication {
        application {
            module(isProd = false, isDev = false)
        }
        
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.get("/api/activities")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
