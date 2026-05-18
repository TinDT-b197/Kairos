package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.RetrofitClient
import com.example.kairos.model.SkillRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SkillState {
    object Idle : SkillState()
    object Loading : SkillState()
    data class Success(val message: String) : SkillState()
    data class Error(val message: String) : SkillState()
}

class SkillViewModel : ViewModel() {
    private val _skillState = MutableStateFlow<SkillState>(SkillState.Idle)
    val skillState: StateFlow<SkillState> = _skillState.asStateFlow()

    // --- 1. HÀM THÊM ---
    fun createSkill(userId: Int, title: String, description: String, priceDiamonds: Int) {
        _skillState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                val request = SkillRequest(
                    userId = userId,
                    title = title,
                    description = description,
                    priceDiamonds = priceDiamonds
                )
                // Gọi thẳng hàm addSkill
                val response = RetrofitClient.apiService.addSkill(request)

                if (response.status == "success") {
                    _skillState.value = SkillState.Success(response.message)
                } else {
                    _skillState.value = SkillState.Error(response.message)
                }
            } catch (e: Exception) {
                _skillState.value = SkillState.Error("Lỗi kết nối mạng: ${e.message}")
            }
        }
    }

    // --- 2. HÀM SỬA ---
    fun editSkill(skillId: Int, userId: Int, title: String, description: String, priceDiamonds: Int) {
        _skillState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                val request = SkillRequest(
                    skillId = skillId,
                    userId = userId,
                    title = title,
                    description = description,
                    priceDiamonds = priceDiamonds
                )
                // Gọi thẳng hàm editSkill
                val response = RetrofitClient.apiService.editSkill(request)

                if (response.status == "success") {
                    _skillState.value = SkillState.Success(response.message)
                } else {
                    _skillState.value = SkillState.Error(response.message)
                }
            } catch (e: Exception) {
                _skillState.value = SkillState.Error("Lỗi kết nối mạng: ${e.message}")
            }
        }
    }

    // --- 3. HÀM XÓA ---
    fun deleteSkill(skillId: Int, userId: Int) {
        _skillState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                // Chỉ cần truyền ID bài và ID người dùng là đủ để xóa
                val request = SkillRequest(skillId = skillId, userId = userId)
                val response = RetrofitClient.apiService.deleteSkill(request)

                if (response.status == "success") {
                    _skillState.value = SkillState.Success("Đã xóa kỹ năng thành công!")
                } else {
                    _skillState.value = SkillState.Error(response.message)
                }
            } catch (e: Exception) {
                _skillState.value = SkillState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _skillState.value = SkillState.Idle
    }
}