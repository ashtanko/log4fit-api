package dev.shtanko

import dev.shtanko.dto.request.RegistrationRequest
import dev.shtanko.dto.response.TokenResponse
import dev.shtanko.plugins.configureRouting
import dev.shtanko.plugins.configureSerialization
import dev.shtanko.service.AuthService
import dev.shtanko.service.TransactionService
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.assertEquals

class AuthRoutesUnitTest {

    @Test
    fun `test POST register route`() = testApplication {
        // 1. Create mocks for all services injected in your routing
        val mockAuthService = mockk<AuthService>()
        val mockTransactionService = mockk<TransactionService>(relaxed = true) // Relaxed mock as we don't use it in this test

        // 2. Define the mock's behavior for this specific test
        val fakeTokenResponse = TokenResponse("fake_access_token", "fake_refresh_token")
        coEvery { mockAuthService.registerUser(any()) } returns fakeTokenResponse

        // 3. Configure the test application with mocks
        application {
            this@testApplication.install(Koin) {
                modules(module {
                    single { mockAuthService } // Provide the mock instead of the real service
                    single { mockTransactionService }
                })
            }
            // Install only what's necessary for this route test
            configureSerialization() // For request/response bodies
            configureRouting()       // To set up the routes
        }

        // 4. Create a test client
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // 5. Execute the request
        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegistrationRequest(
                name = "Test User",
                email = "test@example.com",
                password = "Password123"
            ))
        }

        // 6. Assert the results
        assertEquals(HttpStatusCode.Created, response.status)

        // 7. Verify the service method was called exactly once
        coVerify(exactly = 1) { mockAuthService.registerUser(any()) }
    }
}
