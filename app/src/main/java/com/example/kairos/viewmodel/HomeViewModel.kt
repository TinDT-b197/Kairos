package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.RetrofitClient
import com.example.kairos.model.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // Biến chứa danh sách bài đăng (Chuẩn tên của em)
    private val _skillList = MutableStateFlow<List<Skill>>(emptyList())
    val skillList: StateFlow<List<Skill>> = _skillList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Bổ sung thêm biến báo lỗi để giao diện hiển thị cho người dùng biết
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Hàm gọi mạng để lấy dữ liệu
    fun loadSkills() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                // Gọi đúng hàm fetchAllSkills() của em
                val response = RetrofitClient.apiService.fetchAllSkills()
                if (response.status == "success" && response.data != null) {
                    _skillList.value = response.data
                } else {
                    _errorMessage.value = "Không thể lấy dữ liệu từ máy chủ."
                }
            } catch (e: Exception) {
                // Xử lý lỗi
                _errorMessage.value = "Lỗi mạng hoặc máy chủ chưa bật."
            } finally {
                _isLoading.value = false
            }
        }
    }
}