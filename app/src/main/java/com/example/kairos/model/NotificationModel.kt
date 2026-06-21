package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("notification_id") val notificationId: Int,
    val title: String,
    val message: String,
    val type: String, // "WALLET", "BOOKING", "SYSTEM"
    @SerializedName("is_read") val isRead: Int,
    @SerializedName("created_at") val createdAt: String
)

data class NotificationResponse(
    val status: String,
    val data: List<NotificationModel>?
)