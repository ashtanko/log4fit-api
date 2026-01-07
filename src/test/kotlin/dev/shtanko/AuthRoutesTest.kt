package dev.shtanko

import dev.shtanko.dto.request.LoginRequest
import dev.shtanko.dto.request.RegistrationRequest
import dev.shtanko.dto.response.TokenResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthRoutesTest {

    @Test
    fun `test register, login and get user flow`() = testApplication {
        application {
            module(isProd = false)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 1. Register
        val registerResponse = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest(
                name = "Test User",
                email = "test@example.com",
                password = "Password123"
            ))
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        // 2. Login
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(
                password = "Password123",
                email = "test@example.com"
            ))
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val authResponse = loginResponse.body<TokenResponse>()
        assertNotNull(authResponse.accessToken)
        assertNotNull(authResponse.refreshToken)

        // 3. Get User Info
        val meResponse = client.get("/api/auth/me") {
            header(HttpHeaders.Authorization, "Bearer ${authResponse.accessToken}")
        }
        assertEquals(HttpStatusCode.OK, meResponse.status)
    }

    @Test
    fun `test validation`() = testApplication {
        application {
            module(isProd = false)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Invalid Password
        val invalidPasswordResponse = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest(
                name = "Test User",
                email = "test2@example.com",
                password = "123" // Too short
            ))
        }
        assertEquals(HttpStatusCode.BadRequest, invalidPasswordResponse.status)

        // Invalid Email
        val invalidEmailResponse = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest(
                name = "Test User",
                email = "not-an-email",
                password = "Password123"
            ))
        }
        assertEquals(HttpStatusCode.BadRequest, invalidEmailResponse.status)
    }
}
