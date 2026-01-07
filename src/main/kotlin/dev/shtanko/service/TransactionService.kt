package dev.shtanko.service

import dev.shtanko.dto.response.RecentTransactionResponse
import dev.shtanko.util.AppUtil
import kotlin.random.Random

class TransactionService {
    fun recentTransactions(): List<RecentTransactionResponse> {
        val recent = List(10) {
            RecentTransactionResponse(
                transactionType = AppUtil.randomTransactionType(),
                amount = AppUtil.randomTransactionAmount(),
                time = AppUtil.getRandomDateTimeInLastWeek(),
                status = Random.nextBoolean()
            )
        }
        return recent
    }
}
