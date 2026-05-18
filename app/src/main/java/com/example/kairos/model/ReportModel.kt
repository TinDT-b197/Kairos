package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class ReportModel(
    @SerializedName("transaction_id") val transactionId: Int,
    @SerializedName("reported_by") val reportedBy: Int,
    @SerializedName("reported_user") val reportedUser: Int,
    val reason: String,
    @SerializedName("proof_image") val proofImage: String? = null
)