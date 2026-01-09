package dev.shtanko.plugins

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.FileInputStream

fun Application.configureFirebase() {
    val log = LoggerFactory.getLogger("FirebaseConfig")
    val serviceAccountPath = System.getenv("FIREBASE_SERVICE_ACCOUNT_PATH")
    val serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON")

    try {
        val options = when {
            !serviceAccountJson.isNullOrBlank() -> {
                log.info("Initializing Firebase from JSON environment variable")
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray())))
                    .build()
            }
            !serviceAccountPath.isNullOrBlank() -> {
                log.info("Initializing Firebase from file: $serviceAccountPath")
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(FileInputStream(serviceAccountPath)))
                    .build()
            }
            else -> {
                log.warn("No Firebase service account found. Firebase features will be disabled.")
                null
            }
        }

        if (options != null && FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
            log.info("Firebase initialized successfully")
        }
    } catch (e: Exception) {
        log.error("Failed to initialize Firebase", e)
    }
}
