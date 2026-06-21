package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Thu thập trạng thái từ ViewModel (Giữ nguyên logic)
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToHome()
            authViewModel.resetState()
        }
    }

    // Nền Gradient (Dark Blue -> Light Gray)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A), // Màu xanh đen đậm ở trên
            Color(0xFFCBD5E1)  // Màu xám sáng ở dưới
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // --- HEADER ---
            Text(
                text = "KAIROS",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kết nối kỹ năng, nâng tầm giá trị",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- KHUNG TRẮNG BO GÓC (CARD) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tiêu đề form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Đăng nhập",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "Sử dụng email và mật khẩu để tiếp tục.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )
                    }

                    // Ô nhập Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0F172A),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ô nhập Mật khẩu
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Mật khẩu", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0F172A),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hiển thị trạng thái Lỗi / Loading
                    when (authState) {
                        is AuthState.Loading -> CircularProgressIndicator(color = Color(0xFF0F172A), modifier = Modifier.padding(bottom = 12.dp))
                        is AuthState.Error -> Text(
                            text = (authState as AuthState.Error).message,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        else -> {}
                    }

                    // Nút "Tiếp tục"
                    Button(
                        onClick = { authViewModel.loginUser(email, password, sessionManager) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Text("Tiếp tục", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    // Divider "hoặc"
                    Row(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFF1F5F9))
                        Text(
                            text = "hoặc",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFF1F5F9))
                    }

                    // Nút "Tạo tài khoản mới"
                    OutlinedButton(
                        onClick = { onNavigateToRegister() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2B4C7E)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFCBD5E1)))
                    ) {
                        Text("Tạo tài khoản mới", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- FOOTER ---
            Text(
                text = "Bằng cách tiếp tục, bạn đồng ý với điều khoản dịch vụ và chính sách bảo mật.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 24.dp)
            )
        }
    }
}