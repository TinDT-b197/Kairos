package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.DepositState
import com.example.kairos.viewmodel.WalletViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    onNavigateBack: () -> Unit,
    walletViewModel: WalletViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    // Khởi tạo scrollState cho khả năng cuộn
    val scrollState = rememberScrollState()

    // Danh sách gói kim cương (-1 = "Khác")
    val diamondOptions = listOf(50, 100, 200, 500, -1)
    var selectedOption by remember { mutableIntStateOf(100) }

    // Biến lưu số lượng người dùng tự nhập
    var customAmountStr by remember { mutableStateOf("") }

    // Tính toán số Kim cương thực tế
    val currentDiamonds = if (selectedOption == -1) {
        customAmountStr.toIntOrNull() ?: 0
    } else {
        selectedOption
    }

    val moneyVnd = currentDiamonds * 200.0

    // Validate: Tối thiểu 50 💎
    val isAmountValid = selectedOption != -1 || currentDiamonds >= 50

    // Cấu hình link VietQR động
    val bankCode = "MB"
    val accountNo = "797908022006" // Số tài khoản admin
    val accountName = URLEncoder.encode("DANG TRAN TIN", "UTF-8")
    val transferContent = URLEncoder.encode("KAIROS NAP $userId", "UTF-8")

    val qrUrl = "https://img.vietqr.io/image/$bankCode-$accountNo-compact1.png?amount=${moneyVnd.toInt()}&addInfo=$transferContent&accountName=$accountName"

    val depositState by walletViewModel.depositState.collectAsState()

    LaunchedEffect(depositState) {
        when (depositState) {
            is DepositState.Success -> {
                Toast.makeText(context, (depositState as DepositState.Success).message, Toast.LENGTH_LONG).show()
                walletViewModel.resetState()
                onNavigateBack()
            }
            is DepositState.Error -> {
                Toast.makeText(context, (depositState as DepositState.Error).message, Toast.LENGTH_SHORT).show()
                walletViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nạp Kim Cương 💎", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                // ĐÃ SỬA: Thêm verticalScroll để có thể cuộn màn hình
                .verticalScroll(scrollState)
        ) {
            Text("Chọn số lượng muốn nạp:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))

            // Lưới chọn gói nhanh
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                diamondOptions.take(3).forEach { amount ->
                    DiamondOptionCard(amount, selectedOption, Modifier.weight(1f)) { selectedOption = amount }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                diamondOptions.drop(3).forEach { amount ->
                    DiamondOptionCard(amount, selectedOption, Modifier.weight(1f)) {
                        selectedOption = amount
                        if (amount != -1) customAmountStr = ""
                    }
                }
            }

            // Ô nhập tùy chọn "Khác..."
            if (selectedOption == -1) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = customAmountStr,
                    onValueChange = { customAmountStr = it },
                    label = { Text("Số kim cương muốn nạp") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = !isAmountValid && customAmountStr.isNotEmpty()
                )
                if (!isAmountValid && customAmountStr.isNotEmpty()) {
                    Text(
                        text = "Vui lòng nạp tối thiểu 50 💎",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card QR VietQR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Quét mã để thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                    Text("Mã đã bao gồm sẵn số tiền & nội dung", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentDiamonds > 0) {
                            AsyncImage(
                                model = qrUrl,
                                contentDescription = "Mã QR Nạp tiền",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(200.dp)
                                    .offset(x = 10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F5F5))
                                    .padding(10.dp)
                            )
                        } else {
                            Text("Vui lòng nhập số lượng", color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Số tiền: ", color = Color.Gray, fontSize = 14.sp)
                    Text(String.format("%,.0f VNĐ", moneyVnd), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Text("Nội dung: KAIROS NAP $userId", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nút Xác nhận chuyển tiền
            if (depositState is DepositState.Loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                Button(
                    onClick = { walletViewModel.sendDepositRequest(userId, currentDiamonds, moneyVnd) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    // KHÓA NÚT NẾU CHƯA ĐẠT 50 💎
                    enabled = isAmountValid && currentDiamonds > 0
                ) {
                    Text("Tôi đã chuyển khoản thành công", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Thêm Spacer nhỏ cuối cùng để nút nạp tiền nằm đẹp hơn
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DiamondOptionCard(amount: Int, selectedAmount: Int, modifier: Modifier, onClick: () -> Unit) {
    val isSelected = selectedAmount == amount
    Card(
        modifier = modifier.clickable { onClick() }.height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.Black else Color.White),
        border = BorderStroke(1.dp, if (isSelected) Color.Black else Color(0xFFE0E0E0))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = if (amount == -1) "Khác..." else "$amount 💎",
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}