package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kairos.model.NotificationModel
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    var notifications by remember { mutableStateOf<List<NotificationModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Gọi API lấy thông báo khi mở màn hình
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getNotifications(userId)
            if (response.status == "success") {
                notifications = response.data ?: emptyList()
            }
        } catch (e: Exception) {
            // Xử lý lỗi mạng
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBFBFB))
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có thông báo nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { noti ->
                    NotificationItem(noti)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(noti: NotificationModel) {
    val (icon, bgColor, iconColor) = when (noti.type) {
        "WALLET" -> Triple(Icons.Default.MonetizationOn, Color(0xFFE8F5E9), Color(0xFF4CAF50)) // Xanh lá
        "BOOKING" -> Triple(Icons.Default.EventAvailable, Color(0xFFE3F2FD), Color(0xFF2196F3)) // Xanh dương
        else -> Triple(Icons.Default.Notifications, Color(0xFFFFF3E0), Color(0xFFFF9800)) // Cam cho Hệ thống
    }

    val isUnread = noti.isRead == 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isUnread) Color.White else Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnread) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = noti.title,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = noti.message,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = noti.createdAt,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Chấm đỏ nếu chưa đọc
            if (isUnread) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
            }
        }
    }
}