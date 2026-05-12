package com.example.kairos.network

import com.example.kairos.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KairosApiService {
    @POST("users.php")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("login.php") // THÊM DÒNG NÀY
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("create_skill.php")
    suspend fun createSkill(@Body request: SkillRequest): BaseResponse

    @GET("fetch_skills.php")
    suspend fun fetchAllSkills(): SkillListResponse

    @POST("book_skill.php")
    suspend fun bookSkill(@Body request: BookSkillRequest): BaseResponse

    @GET("fetch_my_bookings.php")
    suspend fun getMyBookings(@Query("user_id") userId: Int): TransactionListResponse

    @POST("confirm_transaction.php")
    suspend fun confirmTransaction(@Body request: ConfirmRequest): BaseResponse

    @GET("fetch_profile.php")
    suspend fun getUserProfile(@Query("user_id") userId: Int): UserProfileResponse

    @GET("fetch_my_sales.php")
    suspend fun getMySales(@Query("user_id") userId: Int): SaleListResponse
}