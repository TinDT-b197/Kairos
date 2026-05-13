package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.ConfirmReviewRequest // Model mới chứa cả ID và Review
import com.example.kairos.network.RetrofitClient
import com.example.kairos.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val _bookings = MutableStateFlow<List<Transaction>>(emptyList())
    val bookings: StateFlow<List<Transaction>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // Lấy danh sách lịch sử học tập
    fun loadBookings(userId: Int) {
        if (userId == -1) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getMyBookings(userId)
                if (response.status == "success") {
                    _bookings.value = response.data ?: emptyList()
                } else {
                    _message.value = "Không thể tải danh sách"
                }
            } catch (e: Exception) {
                _message.value = "Lỗi mạng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun confirmWithReview(transactionId: Int, reviewerId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ConfirmReviewRequest(
                    transaction_id = transactionId,
                    reviewer_id = reviewerId,
                    rating = rating,
                    comment = comment
                )

                val response = RetrofitClient.apiService.confirmTransaction(request)

                if (response.status == "success") {
                    _message.value = "Hoàn tất và đánh giá thành công!"
                    // Tải lại danh sách để Card chuyển sang màu xanh "Hoàn thành"
                    loadBookings(reviewerId)
                } else {
                    _message.value = response.message
                }
            } catch (e: Exception) {
                _message.value = "Lỗi xác nhận: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = ""
    }
}