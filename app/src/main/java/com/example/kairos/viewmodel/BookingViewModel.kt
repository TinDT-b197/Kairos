package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.ConfirmRequest
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Trạng thái khi bấm nút Xác nhận
sealed class ConfirmState {
    object Idle : ConfirmState()
    object Loading : ConfirmState()
    data class Success(val message: String) : ConfirmState()
    data class Error(val message: String) : ConfirmState()
}

class BookingViewModel : ViewModel() {
    // Lưu danh sách lịch sử học tập
    private val _bookings = MutableStateFlow<List<Transaction>>(emptyList())
    val bookings: StateFlow<List<Transaction>> = _bookings.asStateFlow()

    // Lưu trạng thái nút bấm Xác nhận
    private val _confirmState = MutableStateFlow<ConfirmState>(ConfirmState.Idle)
    val confirmState: StateFlow<ConfirmState> = _confirmState.asStateFlow()

    // 1. Lấy danh sách giao dịch của user đang đăng nhập
    fun loadBookings(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyBookings(userId)
                if (response.status == "success" && response.data != null) {
                    _bookings.value = response.data
                }
            } catch (e: Exception) {
                android.util.Log.e("KAIROS_DEBUG", "Lỗi tải lịch sử: ${e.message}")
            }
        }
    }

    // 2. Khi bấm xác nhận: Gọi API -> Cập nhật thành công -> Gọi lại loadBookings để làm mới UI
    fun confirm(transactionId: Int, userId: Int) {
        _confirmState.value = ConfirmState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.confirmTransaction(ConfirmRequest(transactionId))
                if (response.status == "success") {
                    _confirmState.value = ConfirmState.Success(response.message)

                    // TẢI LẠI DANH SÁCH NGAY LẬP TỨC ĐỂ HIỆN CHỮ "HOÀN THÀNH"
                    loadBookings(userId)
                } else {
                    _confirmState.value = ConfirmState.Error(response.message)
                }
            } catch (e: Exception) {
                _confirmState.value = ConfirmState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetConfirmState() {
        _confirmState.value = ConfirmState.Idle
    }
}