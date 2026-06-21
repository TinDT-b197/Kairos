package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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

    // Bảng màu Theme
    val creamBg = Color(0xFFf5eedc)
    val darkBlue = Color(0xFF14213d)
    val tealColor = Color(0xFF20c997)

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

    Column(modifier = Modifier.fillMaxSize().background(creamBg)) {
        // --- HEADER ---
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 24.dp, bottom = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
            }
            Column {
                Text("Hoạt động của tôi", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Theo dõi học tập, giảng dạy và ví tiền", fontSize = 13.sp, color = Color.Gray)
            }
        }

        // --- TAB BAR ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = tealColor,
                    height = 3.dp
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
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selectedTab == index) tealColor else Color.Gray
                        )
                    }
                )
            }
        }

        // --- CONTENT LIST ---
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            when (selectedTab) {
                0 -> {
                    if (isBookingsLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = darkBlue)
                    } else if (bookings.isEmpty()) {
                        Text("Chưa có lịch học nào.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                            items(bookings) { txn ->
                                HistoryCard(
                                    title = txn.title,
                                    subTitle = "Người dạy: ${txn.sellerName}",
                                    price = "${txn.priceDiamonds} xu",
                                    status = txn.status,
                                    isBuyer = true,
                                    onConfirm = {
                                        currentTxnId = txn.transactionId
                                        showRatingDialog = true
                                    },
                                    onChat = { onNavigateToChat(txn.transactionId, txn.sellerName) },
                                    onReport = { onNavigateToReport(txn.transactionId, txn.sellerId) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (isSalesLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = darkBlue)
                    } else if (salesList.isEmpty()) {
                        Text("Chưa có ai đặt lịch dạy.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                            items(salesList) { sale ->
                                HistoryCard(
                                    title = sale.title,
                                    subTitle = "Học viên: ${sale.buyerName}",
                                    price = "+ ${sale.priceDiamonds} xu",
                                    status = sale.status,
                                    isBuyer = false,
                                    onConfirm = {},
                                    onChat = {
                                        val safeName = android.net.Uri.encode(sale.buyerName ?: "Học viên")
                                        onNavigateToChat(sale.transactionId, safeName)
                                    },
                                    onReport = { onNavigateToReport(sale.transactionId, sale.buyerId) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (isWalletLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = darkBlue)
                    } else if (walletList.isEmpty()) {
                        Text("Không có lịch sử giao dịch.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
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

// ---- Các Composable phụ ----

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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Phần trên: Thông tin Khóa học
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subTitle, color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Phần dưới: Các nút Hành động dựa theo trạng thái (status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (status.uppercase()) {
                    "PENDING" -> {
                        if (isBuyer) {
                            // Nút Xác nhận (Dark Blue)
                            Button(
                                onClick = onConfirm,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14213d)),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Text("Xác nhận", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Text("Đang dạy", color = Color(0xFFF57C00), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        // Nút Nhắn tin
                        Row(
                            modifier = Modifier.clickable { onChat() }.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF14213d), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nhắn tin", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213d))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Nút Báo cáo
                        if (isBuyer) {
                            Text(
                                text = "Báo cáo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935),
                                modifier = Modifier.clickable { onReport() }.padding(8.dp)
                            )
                        }
                    }

                    "DISPUTED" -> {
                        // Trạng thái: ĐANG CHỜ KHIẾU NẠI (Màu vàng cam)
                        Row(
                            modifier = Modifier.clickable { onChat() }.padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF14213d), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nhắn tin", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213d))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF3CD), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Đang chờ khiếu nại",
                                color = Color(0xFF856404),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    "CANCELLED" -> {
                        // Trạng thái: ĐÃ HỦY VÀ HOÀN TIỀN (Màu đỏ nhạt)
                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF8D7DA), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Đã hủy & Hoàn tiền",
                                color = Color(0xFF721C24),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    else -> {
                        // Trạng thái mặc định: COMPLETED (Hoàn thành - Màu Xanh ngọc)
                        Row(
                            modifier = Modifier.clickable { onChat() }.padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF14213d), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nhắn tin", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14213d))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFe6f8f3), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Hoàn thành",
                                color = Color(0xFF20c997),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletHistoryItem(history: WalletHistory) {
    val isPositive = history.amount > 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp) // Phẳng giống thiết kế
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isPositive) Color(0xFFe6f8f3) else Color(0xFFFFEBEE)), // Teal nhạt / Đỏ nhạt
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPositive) "+" else "-",
                    color = if (isPositive) Color(0xFF20c997) else Color(0xFFF44336), // Teal / Đỏ
                    fontWeight = FontWeight.Bold, fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = history.description, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = history.date, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${if (isPositive) "+" else ""}${history.amount} xu",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (isPositive) Color(0xFF20c997) else Color(0xFFF44336)
            )
        }
    }
}

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
                                modifier = Modifier.size(36.dp)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF14213d)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14213d)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Gửi & Xác nhận", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Để sau", color = Color.Gray) }
        }
    )
}