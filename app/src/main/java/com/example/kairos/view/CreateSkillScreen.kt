package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.SkillState
import com.example.kairos.viewmodel.SkillViewModel

@Composable
fun CreateSkillScreen(
    skillViewModel: SkillViewModel = viewModel(),
    onNavigateBack: () -> Unit // Lệnh để quay lại trang trước sau khi đăng xong
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    // Lấy ID người dùng từ ngăn kéo (Nếu không có thì mặc định là -1)
    val currentUserId = sessionManager.getUserId()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val skillState by skillViewModel.skillState.collectAsState()
    val scrollState = rememberScrollState()

    // Lắng nghe trạng thái thành công để hiện thông báo và thoát màn hình
    LaunchedEffect(skillState) {
        if (skillState is SkillState.Success) {
            Toast.makeText(context, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
            skillViewModel.resetState()
            onNavigateBack() // Quay về trang chủ
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Header
        Text(text = "Đăng Kỹ Năng Mới", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Chia sẻ kiến thức của bạn để nhận kim cương",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // 1. Ô nhập Tiêu đề
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Kĩ năng bạn muốn dạy", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Ô nhập Mô tả (Cho phép nhập nhiều dòng)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Mô tả chi tiết những gì học viên sẽ nhận được...", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Làm ô này cao lên
            shape = RoundedCornerShape(12.dp),
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Ô nhập Giá kim cương (Chỉ hiện bàn phím số)
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            placeholder = { Text("Giá kim cương (VD: 50)", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hiển thị lỗi hoặc loading
        when (skillState) {
            is SkillState.Loading -> CircularProgressIndicator(color = Color.Black)
            is SkillState.Error -> Text(
                text = (skillState as SkillState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            else -> {}
        }

        // 4. Nút Đăng Bài
        Button(
            onClick = {
                // Kiểm tra xem đã đăng nhập chưa và giá tiền có phải số hợp lệ không
                if (currentUserId != -1) {
                    val priceInt = price.toIntOrNull() ?: 0
                    if (title.isNotEmpty() && description.isNotEmpty() && priceInt > 0) {
                        skillViewModel.createSkill(currentUserId, title, description, priceInt)
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ và hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi: Không tìm thấy ID người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = skillState !is SkillState.Loading
        ) {
            Text("Đăng Kỹ Năng", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nút Hủy
        TextButton(onClick = { onNavigateBack() }) {
            Text("Hủy bỏ", color = Color.Gray)
        }
    }
}