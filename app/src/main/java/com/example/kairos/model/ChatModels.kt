package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("message_id") val messageId: Int,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("sender_name") val senderName: String,
    val message: String,
    @SerializedName("created_at") val createdAt: String
)

data class SendMessageRequest(
    @SerializedName("transaction_id") val transactionId: Int,
    @SerializedName("sender_id") val senderId: Int,
    val message: String
)

data class ChatListResponse(
    val status: String,
    val data: List<ChatMessage>?
)