package com.example.kairos.view

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.AuthState
import com.example.kairos.viewmodel.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel(),
                onNavigateToRegister: () -> Unit,
                onNavigateToHome: () -> Unit) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    // Thu thập trạng thái từ ViewModel
    val authState by authViewModel.authState.collectAsState()
    // Biến lưu thông tin nhập
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Chuyển thẳng vào Trang chủ ngay lập tức
            onNavigateToHome()

            // Reset trạng thái để tránh lỗi khi quay lại
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            // Kích hoạt tính năng cuộn ở đây
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // GIẢM KHOẢNG CÁCH NÀY XUỐNG để Header (KAIROS) đẩy lên cao hơn
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "KAIROS",
            fontSize = 28.sp, // Thu nhỏ lại một chút cho giống mẫu
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Tiêu đề hướng dẫn
        Text(
            text = "Đăng nhập tài khoản",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Nhập email của bạn để tiếp tục sử dụng ứng dụng",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Ô nhập Mật khẩu
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Mật khẩu", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Hiển thị thông báo Lỗi hoặc Loading (nếu có)
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator(color = Color.Black)
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            is AuthState.Success -> Text(
                text = "Đăng nhập thành công!",
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            else -> {}
        }

        // 6. Nút "Tiếp tục" (Màu đen tuyền)
        Button(
            onClick = { authViewModel.loginUser(email, password,sessionManager) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = authState !is AuthState.Loading
        ) {
            Text("Tiếp tục", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // 7. Divider "hoặc"
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
            Text(
                text = "hoặc",
                modifier = Modifier.padding(horizontal = 10.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
        }

        // 8. Nút "Tạo tài khoản" (Viền đen hoặc nền trắng)
        OutlinedButton(
            onClick = { onNavigateToRegister() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Text("Tạo tài khoản mới", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        // 9. Footer: Điều khoản (Text nhỏ dưới cùng)
        Text(
            text = "Bằng cách tiếp tục, bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của chúng tôi",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}