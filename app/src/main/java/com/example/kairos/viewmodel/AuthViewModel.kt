package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.LoginRequest
import com.example.kairos.model.RegisterRequest
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String, val diamonds: Int) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun registerUser(name: String, email: String, pass: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(RegisterRequest(name, email, pass))
                if (response.status == "success") {
                    _authState.value = AuthState.Success(response.message, response.data?.diamondBalance ?: 0)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Không thể kết nối XAMPP: ${e.message}")
            }
        }
    }
    // Trong class AuthViewModel, thêm hàm này:
    fun loginUser(email: String, pass: String, sessionManager: SessionManager) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, pass))
                if (response.status == "success") {
                    // LƯU VÀO NGĂN KÉO KHI THÀNH CÔNG
                    response.data?.let {
                        sessionManager.saveUser(it.userId, it.name)
                    }
                    _authState.value = AuthState.Success(response.message, response.data?.diamondBalance ?: 0)
                } else {
                    _authState.value = AuthState.Error(response.message)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}