package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.network.BookSkillRequest
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TransactionState {
    object Idle : TransactionState()
    object Loading : TransactionState()
    data class Success(val message: String) : TransactionState()
    data class Error(val message: String) : TransactionState()
}

class SkillDetailViewModel : ViewModel() {
    private val _txnState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val txnState: StateFlow<TransactionState> = _txnState.asStateFlow()

    fun bookSkill(buyerId: Int, sellerId: Int, skillId: Int, price: Int) {
        _txnState.value = TransactionState.Loading
        viewModelScope.launch {
            try {
                val request = BookSkillRequest(buyerId, sellerId, skillId, price)
                val response = RetrofitClient.apiService.bookSkill(request)

                if (response.status == "success") {
                    _txnState.value = TransactionState.Success(response.message)
                } else {
                    _txnState.value = TransactionState.Error(response.message)
                }
            } catch (e: Exception) {
                _txnState.value = TransactionState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _txnState.value = TransactionState.Idle
    }
}