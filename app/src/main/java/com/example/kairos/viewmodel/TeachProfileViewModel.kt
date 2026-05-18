package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.Skill
import com.example.kairos.model.UserProfile
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherProfileViewModel : ViewModel() {

    private val _teacherProfile = MutableStateFlow<UserProfile?>(null)
    val teacherProfile: StateFlow<UserProfile?> = _teacherProfile.asStateFlow()

    private val _teacherSkills = MutableStateFlow<List<Skill>>(emptyList())
    val teacherSkills: StateFlow<List<Skill>> = _teacherSkills.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadTeacherData(teacherId: Int) {
        if (_teacherProfile.value?.userId == teacherId) return // Đã load rồi, bỏ qua
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Gọi song song cả profile lẫn danh sách kỹ năng
                val profileResponse = RetrofitClient.apiService.getUserProfile(teacherId)
                if (profileResponse.status == "success") {
                    _teacherProfile.value = profileResponse.data
                } else {
                    _errorMessage.value = "Không thể tải thông tin giảng viên"
                }

                val skillsResponse = RetrofitClient.apiService.getSkills(userId = teacherId)
                if (skillsResponse.status == "success") {
                    _teacherSkills.value = skillsResponse.data ?: emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}