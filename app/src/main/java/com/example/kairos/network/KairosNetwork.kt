package com.example.kairos.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Data nhận về
data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserData?)
data class UserData(
    val user_id: Int,
    val name: String,
    val diamond_balance: Int)

// Data gửi đi
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String)

data class LoginRequest(
    val email: String,
    val password: String)

data class SkillRequest(
    val user_id: Int,
    val title: String,
    val description: String,
    val price_diamonds: Int
)

// Data Class cho một bài kỹ năng
data class Skill(
    val skill_id: Int,
    val title: String,
    val description: String,
    val price_diamonds: Int,
    val author_name: String
)

// Response trả về danh sách kỹ năng
data class SkillListResponse(
    val status: String,
    val data: List<Skill>?
)

data class BookSkillRequest(
    val buyer_id: Int,
    val seller_id: Int,
    val skill_id: Int,
    val price: Int
)

data class Transaction(
    val transaction_id: Int,
    val title: String,
    val seller_name: String,
    val amount: Int,
    val status: String
)

data class TransactionListResponse(val status: String, val data: List<Transaction>?)
data class ConfirmRequest(val transaction_id: Int)

object SharedData {
    var selectedSkill: Skill? = null
}

// Vì API này chỉ trả về status và message (không trả về Data phức tạp),
// em có thể tạo một Response chung đơn giản:
data class BaseResponse(
    val status: String,
    val message: String
)
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
}


object RetrofitClient {
    //private const val BASE_URL = "http://10.0.2.2/kairos_api/"
    private const val BASE_URL = "http://192.168.3.27/kairos_api/"

    val apiService: KairosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KairosApiService::class.java)
    }
}