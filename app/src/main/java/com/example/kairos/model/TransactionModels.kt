package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class BookSkillRequest(
    @SerializedName("buyer_id") val buyerId: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("skill_id") val skillId: Int,
    val price: Int
)

data class LearningTransaction(
    @SerializedName("transaction_id") val transactionId: Int,
    val title: String,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("seller_name") val sellerName: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    val status: String
)

data class LearningTransactionListResponse(
    val status: String,
    val data: List<LearningTransaction>?
)

data class ConfirmReviewRequest(
    @SerializedName("transaction_id") val transaction_id: Int,
    @SerializedName("reviewer_id") val reviewer_id: Int,
    val rating: Int,
    val comment: String
)
data class TeachingTransaction(
    @SerializedName("transaction_id") val transactionId: Int,
    val title: String,
    @SerializedName("buyer_id") val buyerId: Int,
    @SerializedName("buyer_name") val buyerName: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    val status: String
)

data class TeachingListResponse(
    val status: String,
    val data: List<TeachingTransaction>?
)