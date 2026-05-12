package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.ConfirmRequest
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    // 1. Biến chứa danh sách lịch sử
    private val _bookings = MutableStateFlow<List<Transaction>>(emptyList())
    val bookings: StateFlow<List<Transaction>> = _bookings

    // 2. Biến điều khiển vòng xoay Loading (Sửa lỗi đỏ isLoading)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Biến chứa thông báo Toast (Sửa lỗi đỏ message)
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // Lấy danh sách lịch sử từ Database
    fun loadBookings(userId: Int) {
        if (userId == -1) return
        viewModelScope.launch {
            _isLoading.value = true // Bật xoay xoay
            try {
                val response = RetrofitClient.apiService.getMyBookings(userId)
                if (response.status == "success") {
                    _bookings.value = response.data ?: emptyList()
                } else {
                    _message.value = "Không thể tải danh sách lịch sử"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi mạng: ${e.message}"
            } finally {
                _isLoading.value = false // Tắt xoay xoay
            }
        }
    }

    // Xử lý xác nhận hoàn thành (Giải ngân Escrow)
    fun confirmTransaction(transactionId: Int, userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.confirmTransaction(ConfirmRequest(transactionId))
                _message.value = response.message
                if (response.status == "success") {
                    // CỰC KỲ QUAN TRỌNG: Tải lại danh sách để cập nhật trạng thái COMPLETED
                    loadBookings(userId)
                }
            } catch (e: Exception) {
                _message.value = "Lỗi xác nhận: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm xóa thông báo sau khi đã hiển thị xong (Sửa lỗi đỏ clearMessage)
    fun clearMessage() {
        _message.value = ""
    }
}