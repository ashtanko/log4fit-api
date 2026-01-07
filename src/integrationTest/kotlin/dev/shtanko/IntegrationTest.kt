package dev.shtanko

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals

class IntegrationTest {

    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
        }

        @BeforeAll
        @JvmStatic
        fun startDB() {
            postgresContainer.start()
            // Set environment variables for the application to use the container
            System.setProperty("DB_URL", postgresContainer.jdbcUrl)
            System.setProperty("DB_USER", postgresContainer.username)
            System.setProperty("DB_PASSWORD", postgresContainer.password)
            System.setProperty("ENV", "production") // Force production mode to use Postgres
        }

        @AfterAll
        @JvmStatic
        fun stopDB() {
            postgresContainer.stop()
        }
    }

    @Test
    fun `test health check with postgres`() = testApplication {
        application {
            // Pass isProd=true to force Postgres usage
            module(isProd = true)
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
