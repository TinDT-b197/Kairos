package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.BookingViewModel
import com.example.kairos.viewmodel.ConfirmState

@Composable
fun MyBookingsScreen(
    viewModel: BookingViewModel = viewModel(),
    onBack: () -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()
    val confirmState by viewModel.confirmState.collectAsState()

    val context = LocalContext.current
    val userId = SessionManager(context).getUserId()

    // Tải dữ liệu khi vừa vào màn hình
    LaunchedEffect(Unit) {
        if (userId != -1) viewModel.loadBookings(userId)
    }

    // Hiện thông báo khi bấm Xác nhận thành công/thất bại
    LaunchedEffect(confirmState) {
        when (confirmState) {
            is ConfirmState.Success -> {
                Toast.makeText(context, (confirmState as ConfirmState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetConfirmState()
            }
            is ConfirmState.Error -> {
                Toast.makeText(context, (confirmState as ConfirmState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetConfirmState()
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // Nút Back
        TextButton(onClick = { onBack() }, contentPadding = PaddingValues(0.dp)) {
            Text("← Quay lại", color = Color.Gray, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("Lịch sử học tập", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa mua khóa học nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(bookings) { txn ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(txn.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Người dạy: ${txn.seller_name}", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${txn.amount} 💎", fontWeight = FontWeight.SemiBold, color = Color.Black)
                            }

                            // Xử lý nút bấm dựa trên trạng thái
                            if (txn.status == "pending") {
                                Button(
                                    onClick = { viewModel.confirm(txn.transaction_id, userId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = confirmState !is ConfirmState.Loading // Khóa nút khi đang load
                                ) {
                                    Text("Xác nhận", fontSize = 12.sp, color = Color.White)
                                }
                            } else {
                                Text("Hoàn thành", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}