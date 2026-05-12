package com.example.kairos.model

import com.google.gson.annotations.SerializedName

data class Skill(
    @SerializedName("skill_id") val skillId: Int,
    val title: String,
    val description: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int,
    @SerializedName("author_name") val authorName: String
)

data class SkillRequest(
    @SerializedName("user_id") val userId: Int,
    val title: String,
    val description: String,
    @SerializedName("price_diamonds") val priceDiamonds: Int
)

data class SkillListResponse(
    val status: String,
    val data: List<Skill>?
)

// Object dùng chung để truyền dữ liệu giữa các màn hình
object SharedData {
    var selectedSkill: Skill? = null
}