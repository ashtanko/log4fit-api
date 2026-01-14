package dev.shtanko.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

object AppUtil {

    fun generateUUID() = UUID.randomUUID().toString()

    fun generateUniqueDigits(length: Int = 4) = (0..9).shuffled().take(length).joinToString("")

    fun randomTransactionType(): String = listOf("Purchase", "Cash", "Refund").random()

    fun randomTransactionAmount(): Double =
        listOf(20000.00, 21000.00, 22000.00, 23000.00, 24000.00, 25000.00, 26000.00, 27000.00, 28000.00).random()

    fun getRandomDateTimeInLastWeek(): LocalDateTime {
        val now: Instant = Clock.System.now()
        val oneWeekAgo: Instant = now - 7.days
        val randomOffset = Random.nextLong(0, 7.days.inWholeSeconds)
        val randomInstant = oneWeekAgo + randomOffset.seconds
        return randomInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

}
