package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.Skill // Nhớ import model Skill nhé
import com.example.kairos.model.UserProfile
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    // 1. Chứa thông tin User (Tên, Ví, Bio...)
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    // 2. THÊM MỚI: Chứa danh sách kỹ năng của riêng User này
    private val _mySkills = MutableStateFlow<List<Skill>>(emptyList())
    val mySkills: StateFlow<List<Skill>> = _mySkills

    // 3. Trạng thái Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Hàm lấy thông tin Profile
    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getUserProfile(userId)
                if (response.status == "success") {
                    _userProfile.value = response.data
                }
            } catch (e: Exception) {
                // Xử lý lỗi mạng
            } finally {
                _isLoading.value = false
            }
        }
    }

    // THÊM MỚI: Hàm lấy danh sách kỹ năng cá nhân
    fun loadMySkills(userId: Int) {
        viewModelScope.launch {
            try {
                // Lưu ý: Đảm bảo trong KairosApiService em đã có hàm getSkills(userId)
                // truyền tham số Query("user_id") nhé!
                val response = RetrofitClient.apiService.getSkills(userId)
                if (response.status == "success") {
                    _mySkills.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }
    }
}