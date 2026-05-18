package com.example.kairos.model

data class WalletHistory(
    val amount: Int,
    val type: String,
    val description: String,
    val date: String
)

data class WalletHistoryResponse(
    val status: String,
    val data: List<WalletHistory>?
)