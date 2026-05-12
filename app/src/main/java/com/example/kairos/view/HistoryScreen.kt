package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SaleTransaction
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.BookingViewModel

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onNavigateToChat: (Int) -> Unit,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    // Quản lý Tab: 0 là Lịch sử học, 1 là Lịch sử dạy
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Lịch sử học", "Lịch sử dạy")

    // Dữ liệu cho Tab Dạy (lấy trực tiếp vì chưa có ViewModel riêng)
    var salesList by remember { mutableStateOf<List<SaleTransaction>>(emptyList()) }
    var isSalesLoading by remember { mutableStateOf(true) }

    // Dữ liệu cho Tab Học (lấy từ ViewModel)
    val bookings by bookingViewModel.bookings.collectAsState()
    val isBookingsLoading by bookingViewModel.isLoading.collectAsState()
    val message by bookingViewModel.message.collectAsState()

    // Tự động tải dữ liệu khi vào trang hoặc đổi Tab
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            bookingViewModel.loadBookings(userId)
        } else {
            isSalesLoading = true
            try {
                val response = RetrofitClient.apiService.getMySales(userId)
                if (response.status == "success") {
                    salesList = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi tải lịch dạy", Toast.LENGTH_SHORT).show()
            } finally {
                isSalesLoading = false
            }
        }
    }

    if (message.isNotEmpty()) {
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            bookingViewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        // --- HEADER ---
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("← Quay lại", color = Color.Gray)
            }
            Text("Hoạt động của tôi", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // --- TAB SELECTOR ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.Black
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.Black else Color.Gray
                        )
                    }
                )
            }
        }

        // --- CONTENT ---
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            if (selectedTab == 0) {
                // TAB LỊCH SỬ HỌC
                if (isBookingsLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                } else if (bookings.isEmpty()) {
                    Text("Bạn chưa đặt lịch học nào.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(bookings) { txn ->
                            HistoryCard(
                                title = txn.title,
                                subTitle = "Người dạy: ${txn.seller_name}",
                                price = "${txn.price_diamonds} 💎",
                                status = txn.status,
                                isBuyer = true,
                                onConfirm = { bookingViewModel.confirmTransaction(txn.transaction_id, userId) },
                                onChat = { onNavigateToChat(txn.transaction_id) }
                            )
                        }
                    }
                }
            } else {
                // TAB LỊCH SỬ DẠY
                if (isSalesLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                } else if (salesList.isEmpty()) {
                    Text("Chưa có ai đặt lịch của bạn.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(salesList) { sale ->
                            HistoryCard(
                                title = sale.title,
                                subTitle = "Người học: ${sale.buyerName}",
                                price = "+ ${sale.priceDiamonds} 💎",
                                status = sale.status,
                                isBuyer = false,
                                onConfirm = {},
                                onChat = { onNavigateToChat(sale.transactionId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- THIẾT KẾ CARD CHUNG CHO CẢ 2 BÊN ---
@Composable
fun HistoryCard(
    title: String,
    subTitle: String,
    price: String,
    status: String,
    isBuyer: Boolean,
    onConfirm: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subTitle, color = if (isBuyer) Color.Gray else Color(0xFF1976D2), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(price, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (status == "PENDING") {
                    if (isBuyer) {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Xác nhận", fontSize = 12.sp) }
                    } else {
                        Text("Đang dạy", color = Color(0xFFF57C00), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Chờ xác nhận", color = Color.Gray, fontSize = 10.sp)
                    }
                } else {
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "Hoàn thành",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                TextButton(onClick = onChat) {
                    Text("Nhắn tin 💬", fontSize = 12.sp, color = Color.Blue)
                }
            }
        }
    }
}