package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class DepositRequest(
    @SerializedName("user_id") val userId: Int,
    val diamonds: Int,
    @SerializedName("amount_vnd") val amountVnd: Double
)