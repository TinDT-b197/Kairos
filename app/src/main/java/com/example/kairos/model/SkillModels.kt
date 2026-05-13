package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class Skill(
    @SerializedName("skill_id") val skillId: Int,
    @SerializedName("user_id") val sellerId: Int,
    val title: String,
    val description: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    @SerializedName("author_name") val authorName: String,
    @SerializedName("avg_rating") val avgRating: Float = 0f,
    @SerializedName("total_reviews") val totalReviews: Int = 0
)

data class SkillRequest(
    @SerializedName("skill_id") val skillId: Int = 0 ,
    @SerializedName("user_id") val userId: Int,
    val title: String = "",
    val description: String = "",
    @SerializedName("price_diamonds") val priceDiamonds: Int = 0
)

data class SkillListResponse(
    val status: String,
    val data: List<Skill>?
)

// Object dùng chung để truyền dữ liệu giữa các màn hình
object SharedData {
    var selectedSkill: Skill? = null
}