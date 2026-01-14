package dev.shtanko.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import dev.shtanko.repository.TokenRepository
import dev.shtanko.repository.UserRepository
import dev.shtanko.util.DateUtil
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class JwtService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {
    private val jwtSecret = System.getenv("JWT_SECRET") ?: "secret"
    private val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "users"
    private val jwtIssuer = System.getenv("JWT_ISSUER") ?: "http://localhost:8080"
    val jwtRealm = System.getenv("JWT_REALM") ?: "ktor-app"

    val logger = LoggerFactory.getLogger(JwtService::class.java)

    val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(jwtSecret))
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .build()

    fun createAccessToken(email: String, userId: String) = createToken(
        email, userId, expireAt = DateUtil.getExpirationInstantInSeconds(1200) // Increased for better UX during dev
    )

    fun createFreshToken(email: String, userId: String) =
        createToken(
            email, userId, expireAt = DateUtil.getExpirationInstantInDays(30)
        )

    private fun createToken(email: String, userId: String, expireAt: Instant): String {
        val jwtToken = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("email", email)
            .withClaim("userId", userId)
            .withSubject(email)
            .withExpiresAt(expireAt)
            .sign(Algorithm.HMAC256(jwtSecret))
        return jwtToken.toString()
    }

    suspend fun validate(authBearer: String?, credential: JWTCredential, isAccessToken: Boolean = true): JWTPrincipal? {
        if (authBearer == null) return null

        if (!authBearer.contains("Bearer ")) return null

        val jwt = if (authBearer.split(" ").size == 2) authBearer.split(" ")[1] else return null


        val findByToken =
            (if (isAccessToken) tokenRepository.findByToken(jwt) else tokenRepository.findByRefreshToken(jwt)) ?: return null

        if (findByToken.revoked) {
            tokenRepository.revokedAllTokens(findByToken.userId, DateUtil.currentDateTime())
            return null
        }

        if (isTokenExpired(credential)) return null

        val email = extractClaim(credential, "email") ?: credential.payload.subject

        return email?.let {
            val user = userRepository.findUserByEmail(it)
            if (user != null && validAudience(credential)) {
                JWTPrincipal(credential.payload)
            } else null
        }

    }

    private fun extractClaim(credential: JWTCredential, key: String = "username"): String? {
        return credential.payload.getClaim(key).asString()
    }

    private fun validAudience(credential: JWTCredential): Boolean =
        credential.payload.audience.contains(jwtAudience)


    private fun isTokenExpired(credential: JWTCredential): Boolean =
        credential.payload.expiresAtAsInstant.isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC))

    companion object {
        fun RoutingCall.getUsername(): String? {
            return this.principal<JWTPrincipal>()?.payload?.subject
        }
    }
}
