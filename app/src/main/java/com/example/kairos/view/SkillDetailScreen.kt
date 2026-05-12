package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.kairos.model.Skill
import com.example.kairos.viewmodel.SkillDetailViewModel
import com.example.kairos.viewmodel.TransactionState

@Composable
fun SkillDetailScreen(
    skill: Skill, // Nhận cục data Skill từ trang chủ truyền sang
    viewModel: SkillDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val buyerId = sessionManager.getUserId()

    val txnState by viewModel.txnState.collectAsState()

    // Lắng nghe trạng thái giao dịch
    LaunchedEffect(txnState) {
        if (txnState is TransactionState.Success) {
            Toast.makeText(context, (txnState as TransactionState.Success).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
            onNavigateBack() // Quay về trang chủ sau khi mua thành công
        } else if (txnState is TransactionState.Error) {
            Toast.makeText(context, (txnState as TransactionState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Nút Back
        TextButton(onClick = { onNavigateBack() }, contentPadding = PaddingValues(0.dp)) {
            Text("← Quay lại", color = Color.Gray, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Chi tiết bài đăng
        Text(text = skill.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Đăng bởi: ${skill.authorName}", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Mô tả chi tiết", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = skill.description, fontSize = 16.sp, color = Color.DarkGray, lineHeight = 24.sp)

        Spacer(modifier = Modifier.weight(1f)) // Đẩy phần thanh toán xuống đáy

        // Khu vực thanh toán
        Surface(
            color = Color(0xFFF8F9FA),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Giá khóa học", color = Color.Gray, fontSize = 14.sp)
                    Text("${skill.priceDiamonds} 💎", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // Kiểm tra không cho phép tự mua bài của chính mình
                        // Giả sử bảng Skills có lưu seller_id, ở đây anh dùng ID mặc định là 1 để demo
                        // Trong thực tế, class Skill của em cần có thêm trường seller_id
                        val sellerId = 1
                        if (buyerId != sellerId) {
                            viewModel.bookSkill(buyerId, sellerId, skill.skillId, skill.priceDiamonds)
                        } else {
                            Toast.makeText(context, "Bạn không thể tự mua kỹ năng của mình!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = txnState !is TransactionState.Loading
                ) {
                    if (txnState is TransactionState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Mua ngay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}