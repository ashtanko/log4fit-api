package dev.shtanko.dto.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RecentTransactionResponse(
    val transactionType:String,
    val amount:Double,
    val time: LocalDateTime,
    val status: Boolean  //True | False
)
