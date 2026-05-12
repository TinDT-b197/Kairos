package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class BookSkillRequest(
    @SerializedName("buyer_id") val buyerId: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("skill_id") val skillId: Int,
    val price: Int
)

data class Transaction(
    @SerializedName("transaction_id") val transactionId: Int,
    val title: String,
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    val status: String
)

data class TransactionListResponse(
    val status: String,
    val data: List<Transaction>?
)

data class ConfirmRequest(
    @SerializedName("transaction_id") val transactionId: Int
)

data class SaleTransaction(
    @SerializedName("transaction_id") val transactionId: Int,
    val title: String,
    @SerializedName("buyer_name") val buyerName: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    val status: String
)

data class SaleListResponse(
    val status: String,
    val data: List<SaleTransaction>?
)