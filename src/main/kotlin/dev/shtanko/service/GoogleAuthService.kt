package dev.shtanko.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.slf4j.LoggerFactory

class GoogleAuthService {
    private val log = LoggerFactory.getLogger(GoogleAuthService::class.java)

    fun verifyToken(token: String): FirebaseToken? {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(token)
        } catch (e: Exception) {
            log.error("Error verifying Firebase token", e)
            null
        }
    }
}
