package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.DepositRequest
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DepositState {
    object Idle : DepositState()
    object Loading : DepositState()
    data class Success(val message: String) : DepositState()
    data class Error(val message: String) : DepositState()
}

class WalletViewModel : ViewModel() {
    private val _depositState = MutableStateFlow<DepositState>(DepositState.Idle)
    val depositState: StateFlow<DepositState> = _depositState.asStateFlow()

    fun sendDepositRequest(userId: Int, diamonds: Int, amountVnd: Double) {
        _depositState.value = DepositState.Loading
        viewModelScope.launch {
            try {
                val request = DepositRequest(userId, diamonds, amountVnd)
                val response = RetrofitClient.apiService.requestDeposit(request)
                if (response.status == "success") {
                    _depositState.value = DepositState.Success(response.message)
                } else {
                    _depositState.value = DepositState.Error(response.message)
                }
            } catch (e: Exception) {
                _depositState.value = DepositState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _depositState.value = DepositState.Idle
    }
}