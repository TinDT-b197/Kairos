package com.example.kairos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kairos.model.ChatMessage
import com.example.kairos.model.SendMessageRequest
import com.example.kairos.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var pollingJob: Job? = null

    // Bắt đầu polling mỗi 3 giây
    fun startPolling(transactionId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                fetchMessages(transactionId)
                delay(3000L)
            }
        }
    }

    // Dừng polling khi thoát màn hình
    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun fetchMessages(transactionId: Int) {
        try {
            val response = RetrofitClient.apiService.getChatMessages(transactionId)
            if (response.status == "success") {
                _messages.value = response.data ?: emptyList()
            }
        } catch (e: Exception) {
            // Không hiển thị lỗi mạng khi polling để tránh spam UI
        }
    }

    fun sendMessage(transactionId: Int, senderId: Int, message: String) {
        if (message.isBlank()) return
        _isSending.value = true
        viewModelScope.launch {
            try {
                val request = SendMessageRequest(transactionId, senderId, message)
                val response = RetrofitClient.apiService.sendChatMessage(request)
                if (response.status == "success") {
                    // Fetch ngay lập tức sau khi gửi để thấy tin nhắn mới
                    fetchMessages(transactionId)
                } else {
                    _errorMessage.value = "Gửi thất bại: ${response.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}