package com.example.kairos.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.BookingViewModel
import com.example.kairos.viewmodel.SkillState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    transactionId: Int,
    reportedUser: Int,
    onNavigateBack: () -> Unit,
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val reportedBy = sessionManager.getUserId()

    var reason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Biến lưu trữ Uri của ảnh được chọn
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher để mở bộ sưu tập ảnh
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val reportState by bookingViewModel.reportState.collectAsState()

    LaunchedEffect(reportState) {
        when (reportState) {
            is SkillState.Success -> {
                Toast.makeText(context, (reportState as SkillState.Success).message, Toast.LENGTH_SHORT).show()
                bookingViewModel.resetReportState()
                onNavigateBack()
            }
            is SkillState.Error -> {
                Toast.makeText(context, (reportState as SkillState.Error).message, Toast.LENGTH_SHORT).show()
                bookingViewModel.resetReportState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Báo cáo giao dịch", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Quay lại") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Vui lòng mô tả chi tiết vấn đề bạn gặp phải. Giao dịch này sẽ bị tạm dừng để Admin xem xét.", color = Color.Gray)

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it; isError = false },
                label = { Text("Lý do báo cáo") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5,
                isError = isError,
                supportingText = { if (isError) Text("Vui lòng nhập lý do", color = Color.Red) }
            )

            // --- KHU VỰC HIỂN THỊ VÀ CHỌN ẢNH ---
            if (selectedImageUri == null) {
                // Nút chọn ảnh
                OutlinedButton(
                    onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Thêm ảnh")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đính kèm ảnh bằng chứng (Tùy chọn)")
                }
            } else {
                // Khung hiển thị ảnh đã chọn
                Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp))) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Bằng chứng",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Nút Xóa ảnh
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (reason.trim().isEmpty()) {
                        isError = true
                    } else {
                        // Truyền thêm context và imageUri vào
                        bookingViewModel.submitReport(context, transactionId, reportedBy, reportedUser, reason.trim(), selectedImageUri)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                enabled = reportState !is SkillState.Loading
            ) {
                if (reportState is SkillState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Gửi Báo Cáo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}