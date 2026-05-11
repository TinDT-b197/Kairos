package com.example.kairos.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.viewmodel.AuthState
import com.example.kairos.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(authViewModel: AuthViewModel = viewModel(),
                   onNavigateToLogin: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Khi đăng ký thành công và nhận được 300 kim cương
            // Chúng ta đợi khoảng 1.5 giây để người dùng kịp đọc thông báo thành công
            kotlinx.coroutines.delay(1500)

            // Sau đó tự động nhảy sang trang Đăng nhập
            onNavigateToLogin()

            // Reset lại trạng thái để tránh việc quay lại màn hình này bị nhảy trang tiếp
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text("KAIROS", fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)

        Spacer(modifier = Modifier.height(40.dp))

        Text("Tạo tài khoản", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Enter your details to sign up for KAIROS",
            fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        // Ô nhập Tên
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            placeholder = { Text("Họ và tên", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ô nhập Email
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ô nhập Mật khẩu
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            placeholder = { Text("Mật khẩu", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hiển thị trạng thái (Đã gom chung lại cho gọn)
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator(color = Color.Black)
            is AuthState.Error -> Text((authState as AuthState.Error).message, color = Color.Red, fontSize = 12.sp)
            is AuthState.Success -> Text(
                text = "Đăng ký thành công! Bạn nhận được 300 💎\nĐang chuyển sang trang Đăng nhập...",
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng ký
        Button(
            onClick = { authViewModel.registerUser(name, email, password) },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = authState !is AuthState.Loading
        ) {
            Text("Create an account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = { onNavigateToLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Text("Đăng nhập", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // Thay weight(1f) bằng height cố định để không bị Crash khi Scroll
        Spacer(modifier = Modifier.height(40.dp))

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