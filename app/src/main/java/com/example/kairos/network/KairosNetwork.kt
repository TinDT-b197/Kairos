package com.example.kairos.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Data nhận về
data class BaseResponse(
    val status: String,
    val message: String
)

// 2. Dữ liệu gửi đi khi Đăng nhập/Đăng ký
data class LoginRequest(
    val email: String,
    val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

data class AuthResponse(
    val status: String,
    val message: String,
    val data: UserData?
)

// Khớp với bảng Users
data class UserData(
    val user_id: Int,
    val name: String,
    val diamond_balance: Int
)

// 3. Khớp với bảng Skills (Thêm author_name từ lệnh JOIN bảng Users)
data class Skill(
    val skill_id: Int,
    val title: String,
    val description: String,
    val price_diamonds: Int, // Đã khớp đúng tên cột trong DB
    val author_name: String
)

data class SkillRequest(
    val user_id: Int,
    val title: String,
    val description: String,
    val price_diamonds: Int // Đã khớp đúng tên cột trong DB
)

data class SkillListResponse(
    val status: String,
    val data: List<Skill>?
)

// 4. Khớp với bảng Transactions (Luồng Escrow)
data class BookSkillRequest(
    val buyer_id: Int,
    val seller_id: Int,
    val skill_id: Int,
    val price: Int
)

data class Transaction(
    val transaction_id: Int,
    val title: String, // Tên kỹ năng lấy từ bảng Skills
    val seller_name: String, // Tên người bán lấy từ bảng Users
    val price_diamonds: Int, // SỬA LẠI: Bỏ amount, dùng chuẩn price_diamonds của DB
    val status: String // PENDING, IN_PROGRESS, COMPLETED...
)

data class TransactionListResponse(
    val status: String,
    val data: List<Transaction>?
)

data class ConfirmRequest(
    val transaction_id: Int
)

object SharedData {
    var selectedSkill: Skill? = null
}

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

data class SaleTransaction(
    @SerializedName("transaction_id") val transactionId: Int,
    val title: String,
    @SerializedName("buyer_name") val buyerName: String, // Hiển thị tên người học
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    val status: String
)

data class SaleListResponse(
    val status: String,
    val data: List<SaleTransaction>?
)

// Vì API này chỉ trả về status và message (không trả về Data phức tạp),
// em có thể tạo một Response chung đơn giản:
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


object RetrofitClient {
    //private const val BASE_URL = "http://10.0.2.2/kairos_api/"
    private const val BASE_URL = "http://192.168.3.88/kairos_api/"

    val apiService: KairosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KairosApiService::class.java)
    }
}