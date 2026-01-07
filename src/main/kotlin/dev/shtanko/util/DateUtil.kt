package dev.shtanko.util

import kotlinx.datetime.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object DateUtil {
    fun currentDateTime() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

    fun getExpirationInstantInMinutes(minutes: Long): Instant {
        return LocalDateTime.now().plusMinutes(minutes).toInstant(
            ZoneOffset.UTC
        )
    }

    fun getExpirationInstantInSeconds(seconds: Long): Instant {
        return LocalDateTime.now().plusSeconds(seconds).toInstant(
            ZoneOffset.UTC
        )
    }

    fun getExpirationInstantInDays(days: Long): Instant {
        return LocalDateTime.now().plusDays(days).toInstant(
            ZoneOffset.UTC
        )
    }
}
