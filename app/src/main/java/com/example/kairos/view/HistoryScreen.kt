package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.RetrofitClient
import com.example.kairos.model.TeachingTransaction
import com.example.kairos.model.WalletHistory
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onNavigateToChat: (transactionId: Int, partnerName: String) -> Unit,
    onNavigateToReport: (transactionId: Int, reportedUserId: Int) -> Unit,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Học tập", "Giảng dạy", "Ví tiền")

    var showRatingDialog by remember { mutableStateOf(false) }
    var currentTxnId by remember { mutableIntStateOf(-1) }

    val bookings by bookingViewModel.bookings.collectAsState()
    val isBookingsLoading by bookingViewModel.isLoading.collectAsState()
    val message by bookingViewModel.message.collectAsState()

    var salesList by remember { mutableStateOf<List<TeachingTransaction>>(emptyList()) }
    var isSalesLoading by remember { mutableStateOf(false) }
    var walletList by remember { mutableStateOf<List<WalletHistory>>(emptyList()) }
    var isWalletLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            0 -> bookingViewModel.loadBookings(userId)
            1 -> {
                isSalesLoading = true
                try {
                    val response = RetrofitClient.apiService.getMySales(userId)
                    if (response.status == "success") salesList = response.data ?: emptyList()
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi tải lịch dạy", Toast.LENGTH_SHORT).show()
                } finally { isSalesLoading = false }
            }
            2 -> {
                isWalletLoading = true
                try {
                    val response = RetrofitClient.apiService.getWalletHistory(userId)
                    if (response.status == "success") walletList = response.data ?: emptyList()
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi tải ví", Toast.LENGTH_SHORT).show()
                } finally { isWalletLoading = false }
            }
        }
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            bookingViewModel.clearMessage()
        }
    }

    if (showRatingDialog) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onSubmit = { rating, comment ->
                bookingViewModel.confirmWithReview(
                    transactionId = currentTxnId,
                    reviewerId = userId,
                    rating = rating,
                    comment = comment
                )
                showRatingDialog = false
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Gray)
            }
            Text("Hoạt động của tôi", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

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
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            when (selectedTab) {
                0 -> {
                    if (isBookingsLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                    } else if (bookings.isEmpty()) {
                        Text("Chưa có lịch học nào.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(bookings) { txn ->
                                HistoryCard(
                                    title = txn.title,
                                    subTitle = "Người dạy: ${txn.sellerName}",
                                    price = "${txn.priceDiamonds} 💎",
                                    status = txn.status,
                                    isBuyer = true,
                                    onConfirm = {
                                        currentTxnId = txn.transactionId
                                        showRatingDialog = true
                                    },
                                    // Truyền partnerName = tên người dạy
                                    onChat = { onNavigateToChat(txn.transactionId, txn.sellerName) },
                                    onReport = { onNavigateToReport(txn.transactionId, txn.sellerId) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (isSalesLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                    } else if (salesList.isEmpty()) {
                        Text("Chưa có ai đặt lịch dạy.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(salesList) { sale ->
                                HistoryCard(
                                    title = sale.title,
                                    subTitle = "Học viên: ${sale.buyerName}",
                                    price = "+ ${sale.priceDiamonds} 💎",
                                    status = sale.status,
                                    isBuyer = false,
                                    onConfirm = {},
                                    // Truyền partnerName = tên học viên
                                    onChat = { onNavigateToChat(sale.transactionId, sale.buyerName) },
                                    onReport = { onNavigateToReport(sale.transactionId, sale.buyerId) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (isWalletLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
                    } else if (walletList.isEmpty()) {
                        Text("Không có lịch sử giao dịch.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(walletList) { item ->
                                WalletHistoryItem(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---- Các Composable phụ (giữ nguyên từ bản cũ) ----

@Composable
fun RatingDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đánh giá khóa học", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Vui lòng đánh giá để hoàn tất chuyển kim cương cho người dạy", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    (1..5).forEach { index ->
                        IconButton(onClick = { rating = index }) {
                            Icon(
                                imageVector = if (index <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Nhập nhận xét (không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) { Text("Gửi & Xác nhận") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Để sau", color = Color.Gray) }
        }
    )
}

@Composable
fun WalletHistoryItem(history: WalletHistory) {
    val isPositive = history.amount > 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(if (isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPositive) "+" else "-",
                    color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = history.description, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = history.date, fontSize = 11.sp, color = Color.Gray)
            }
            Text(
                text = "${if (isPositive) "+" else ""}${history.amount} 💎",
                fontWeight = FontWeight.ExtraBold,
                color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun HistoryCard(
    title: String, subTitle: String, price: String, status: String,
    isBuyer: Boolean, onConfirm: () -> Unit, onChat: () -> Unit,
    onReport: () -> Unit
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
                    }
                } else {
                    Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            "Hoàn thành", color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                TextButton(onClick = onChat) {
                    Text("Nhắn tin 💬", fontSize = 12.sp, color = Color.Blue)
                }
                if (status.equals("PENDING", ignoreCase = true)) {
                    TextButton(onClick = onReport) {
                        Text("Báo cáo", fontSize = 12.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}