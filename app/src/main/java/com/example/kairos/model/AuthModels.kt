package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    val status: String,
    val message: String
)

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserData?
)

data class UserData(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    @SerializedName("diamond_balance") val diamondBalance: Int
)

data class UserProfile(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val avatar: String?,
    val bio: String?,
    @SerializedName("diamond_balance") val diamondBalance: Int
)

data class UserProfileResponse(
    val status: String,
    val data: UserProfile?
)