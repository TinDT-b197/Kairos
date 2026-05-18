package com.example.kairos.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.ConfirmReviewRequest // Model mới chứa cả ID và Review
import com.example.kairos.model.LearningTransaction
import com.example.kairos.model.ReportModel
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Base64

class BookingViewModel : ViewModel() {
    private val _bookings = MutableStateFlow<List<LearningTransaction>>(emptyList())
    val bookings: StateFlow<List<LearningTransaction>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reportState = MutableStateFlow<SkillState>(SkillState.Idle)
    val reportState: StateFlow<SkillState> = _reportState.asStateFlow()
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

    fun submitReport(transactionId: Int, reportedBy: Int, reportedUser: Int, reason: String) {
        _reportState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                val reportData = ReportModel(transactionId, reportedBy, reportedUser, reason)
                val response = RetrofitClient.apiService.submitReport(reportData)

                if (response.status == "success") {
                    _reportState.value = SkillState.Success(response.message)
                } else {
                    _reportState.value = SkillState.Error(response.message)
                }
            } catch (e: Exception) {
                _reportState.value = SkillState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }
    private fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Cập nhật hàm submitReport để nhận Uri và Context
    fun submitReport(context: Context, transactionId: Int, reportedBy: Int, reportedUser: Int, reason: String, imageUri: Uri?) {
        _reportState.value = SkillState.Loading
        viewModelScope.launch {
            try {
                // Dịch ảnh sang Base64 (nếu có ảnh)
                val base64Image = imageUri?.let { uriToBase64(context, it) }

                // Đóng gói vào ReportModel đúng chuẩn MVVM
                val reportData = ReportModel(
                    transactionId = transactionId,
                    reportedBy = reportedBy,
                    reportedUser = reportedUser,
                    reason = reason,
                    proofImage = base64Image
                )

                // Gọi API bằng cục dữ liệu JSON này
                val response = RetrofitClient.apiService.submitReport(reportData)

                if (response.status == "success") {
                    _reportState.value = SkillState.Success(response.message)
                } else {
                    _reportState.value = SkillState.Error(response.message)
                }
            } catch (e: Exception) {
                _reportState.value = SkillState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetReportState() {
        _reportState.value = SkillState.Idle
    }

    fun clearMessage() {
        _message.value = ""
    }
}