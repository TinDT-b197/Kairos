package com.example.kairos.network

import com.example.kairos.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KairosApiService {
    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    @POST("login.php") // THÊM DÒNG NÀY
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("manage_skill.php?action=add")
    suspend fun addSkill(@Body request: SkillRequest): BaseResponse

    @POST("manage_skill.php?action=edit")
    suspend fun editSkill(@Body request: SkillRequest): BaseResponse

    @POST("manage_skill.php?action=delete")
    suspend fun deleteSkill(@Body request: SkillRequest): BaseResponse
    @GET("fetch_skills.php")
    suspend fun fetchAllSkills(): SkillListResponse

    @POST("book_skill.php")
    suspend fun bookSkill(@Body request: BookSkillRequest): BaseResponse

    @GET("fetch_my_bookings.php")
    suspend fun getMyBookings(@Query("user_id") userId: Int): LearningTransactionListResponse

    @POST("confirm_transaction.php")
    suspend fun confirmTransaction(@Body request: ConfirmReviewRequest): BaseResponse

    @GET("users.php")
    suspend fun getUserProfile(@Query("user_id") userId: Int): UserProfileResponse

    @POST("users.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): BaseResponse

    @GET("fetch_my_sales.php")
    suspend fun getMySales(@Query("user_id") userId: Int): TeachingListResponse

    @GET("fetch_wallet_history.php")
    suspend fun getWalletHistory(@Query("user_id") userId: Int): WalletHistoryResponse

    @POST("submit_report.php")
    suspend fun submitReport(@Body request: ReportModel): BaseResponse

    @GET("fetch_skills.php")
    suspend fun getSkills(
        @Query("user_id") userId: Int? = null): SkillListResponse

    @GET("fetch_chat.php")
    suspend fun getChatMessages(
        @Query("transaction_id") transactionId: Int
    ): ChatListResponse

    @POST("send_chat.php")
    suspend fun sendChatMessage(@Body request: SendMessageRequest): BaseResponse

    @POST("request_deposit.php")
    suspend fun requestDeposit(@Body request: DepositRequest): BaseResponse

    @GET("fetch_notifications.php")
    suspend fun getNotifications(@Query("user_id") userId: Int): NotificationResponse
}