package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SkillRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Định nghĩa các trạng thái của màn hình Đăng bài
sealed class SkillState {
    object Idle : SkillState()
    object Loading : SkillState()
    data class Success(val message: String) : SkillState()
    data class Error(val message: String) : SkillState()
}

class SkillViewModel : ViewModel() {
    private val _skillState = MutableStateFlow<SkillState>(SkillState.Idle)
    val skillState: StateFlow<SkillState> = _skillState.asStateFlow()

    // 2. Hàm gửi dữ liệu lên XAMPP
    fun createSkill(userId: Int, title: String, description: String, priceDiamonds: Int) {
        _skillState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                // Đóng gói dữ liệu
                val request = SkillRequest(userId, title, description, priceDiamonds)
                // Gọi API
                val response = RetrofitClient.apiService.createSkill(request)

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

    // 3. Hàm reset trạng thái
    fun resetState() {
        _skillState.value = SkillState.Idle
    }
}