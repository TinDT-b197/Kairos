package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    // Biến chứa danh sách bài đăng
    private val _skillList = MutableStateFlow<List<Skill>>(emptyList())
    val skillList: StateFlow<List<Skill>> = _skillList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Hàm gọi mạng để lấy dữ liệu
    fun loadSkills() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.fetchAllSkills()
                if (response.status == "success" && response.data != null) {
                    _skillList.value = response.data
                }
            } catch (e: Exception) {
                // Xử lý lỗi (Có thể thêm state báo lỗi nếu cần)
            } finally {
                _isLoading.value = false
            }
        }
    }
}