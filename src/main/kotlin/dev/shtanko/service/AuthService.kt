package dev.shtanko.service

import dev.shtanko.dto.request.GoogleLoginRequest
import dev.shtanko.dto.request.LoginRequest
import dev.shtanko.dto.request.RegistrationRequest
import dev.shtanko.dto.response.AboutResponse
import dev.shtanko.dto.response.TokenResponse
import dev.shtanko.model.ExposedToken
import dev.shtanko.model.ExposedUser
import dev.shtanko.repository.TokenRepository
import dev.shtanko.repository.UserRepository
import dev.shtanko.util.AppUtil
import dev.shtanko.util.ConflictException
import dev.shtanko.util.DateUtil
import dev.shtanko.util.NotFoundException
import dev.shtanko.util.UnauthorizedException
import dev.shtanko.util.passwordMatches
import dev.shtanko.util.toHashString
import dev.shtanko.util.AppUtil.generateUniqueDigits

class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val jwtService: JwtService,
    private val googleAuthService: GoogleAuthService
) {

    suspend fun registerUser(registrationRequest: RegistrationRequest): TokenResponse {
            val email = registrationRequest.email
            if (userRepository.emailExist(email)) {
                throw ConflictException("Email is in-use")
            }

            val userId = AppUtil.generateUUID()
            userRepository.addUser(
                ExposedUser(
                    id = userId,
                    name = registrationRequest.name,
                    email = email,
                    password = registrationRequest.password.toHashString()
                )
            )

            val accessTokenValue = jwtService.createAccessToken(email, userId)
            val refreshTokenValue = jwtService.createFreshToken(email, userId)
            tokenRepository.revokedAllTokens(userId, DateUtil.currentDateTime())
            tokenRepository.save(
                ExposedToken(
                    token = accessTokenValue,
                    refreshToken = refreshTokenValue,
                    userId = userId
                )
            )

            return TokenResponse(
                accessToken = accessTokenValue,
                refreshToken = refreshTokenValue,
            )

    }

    suspend fun login(loginRequest: LoginRequest): TokenResponse {
        val email = loginRequest.email
        val user = userRepository.findUserByEmail(email)
        if (user == null || !passwordMatches(loginRequest.password, user.password)) {
            throw UnauthorizedException("Invalid Email or password!")
        }

        val accessTokenValue = jwtService.createAccessToken(email, user.id)
        val refreshTokenValue = jwtService.createFreshToken(email, user.id)
        tokenRepository.revokedAllTokens(user.id, DateUtil.currentDateTime())
        tokenRepository.save(
            ExposedToken(
                token = accessTokenValue,
                refreshToken = refreshTokenValue,
                userId = user.id
            )
        )

        return TokenResponse(
            accessToken = accessTokenValue,
            refreshToken = refreshTokenValue,
        )
    }

    suspend fun loginWithGoogle(request: GoogleLoginRequest): TokenResponse {
        val firebaseToken = googleAuthService.verifyToken(request.idToken) ?: throw UnauthorizedException("Invalid Google Token")
        val email = firebaseToken.email ?: throw UnauthorizedException("Google Token missing email")
        val name = firebaseToken.name ?: "Google User"

        var user = userRepository.findUserByEmail(email)
        if (user == null) {
            // Create new user
            val userId = AppUtil.generateUUID()
            userRepository.addUser(
                ExposedUser(
                    id = userId,
                    name = name,
                    email = email,
                    password = "" // No password for Google users
                )
            )
            user = userRepository.findUserByEmail(email)!!
        }

        val accessTokenValue = jwtService.createAccessToken(email, user.id)
        val refreshTokenValue = jwtService.createFreshToken(email, user.id)
        tokenRepository.revokedAllTokens(user.id, DateUtil.currentDateTime())
        tokenRepository.save(
            ExposedToken(
                token = accessTokenValue,
                refreshToken = refreshTokenValue,
                userId = user.id
            )
        )

        return TokenResponse(
            accessToken = accessTokenValue,
            refreshToken = refreshTokenValue,
        )
    }

    suspend fun authMe(username: String): AboutResponse {
        val user = userRepository.findUserByEmail(username)
        return user?.let { exposedUser ->
            AboutResponse(
                name = exposedUser.name,
                email = exposedUser.email,
                sessionId = generateUniqueDigits()
            )

        } ?: throw NotFoundException()
    }

    suspend fun refreshToken(username: String): TokenResponse {
        return userRepository.findUserByEmail(username)?.let {
            tokenRepository.revokedAllTokens(it.id, DateUtil.currentDateTime())
            val accessToken = jwtService.createAccessToken(username, it.id)
            val refreshToken = jwtService.createFreshToken(username, it.id)
            tokenRepository.save(
                ExposedToken(
                    token = accessToken,
                    refreshToken = refreshToken,
                    userId = it.id,
                    revoked = false
                )
            )
            TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )

        } ?: throw NotFoundException()
    }
}
