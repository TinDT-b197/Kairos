package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SessionManager
import com.example.kairos.model.UserProfile
import com.example.kairos.model.Skill // Đảm bảo đã có model Skill
import com.example.kairos.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToMySkill: () -> Unit,
    onNavigateToHistory: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Quan sát dữ liệu từ ViewModel
    val profileData by profileViewModel.userProfile.collectAsState()
    val mySkills by profileViewModel.mySkills.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
    )

    // Tải dữ liệu khi màn hình mở ra
    LaunchedEffect(Unit) {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            profileViewModel.loadProfile(userId)
            profileViewModel.loadMySkills(userId) // Hàm này gọi fetch_skills.php?user_id=...
        }
    }

    if (isLoading && profileData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1976D2))
        }
        return
    }

    profileData?.let { user ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F9FF)),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // --- 1. HEADER ---
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(260.dp).background(blueGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.name.first().toString().uppercase(),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(user.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(user.email, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }

            // --- 2. VÍ TIỀN ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-40).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Số dư Ví KAIROS", color = Color.Gray, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${user.diamondBalance}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D47A1))
                                Text(" 💎", fontSize = 20.sp)
                            }
                        }
                        Button(
                            onClick = { /* Nạp tiền */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Nạp thêm", fontSize = 12.sp) }
                    }
                }
            }

            // --- 3. BIO ---
            item {
                ProfileSection(title = "Giới thiệu bản thân") {
                    Text(
                        text = user.bio ?: "Chưa có thông tin giới thiệu.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            // --- 4. KỸ NĂNG CỦA TÔI (DỮ LIỆU THẬT) ---
            item {
                ProfileSection(title = "Kỹ năng của tôi") {
                    if (mySkills.isEmpty()) {
                        Text("Bạn chưa đăng kỹ năng nào.", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        // CHUNKED(2): Chia danh sách thành từng hàng 2 cái
                        mySkills.chunked(2).forEach { rowSkills ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowSkills.forEach { skill ->
                                    SmallSkillItem(
                                        name = skill.title,
                                        rating = "${String.format("%.1f", skill.avgRating)} ⭐",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Nếu hàng chỉ có 1 cái, thêm Spacer để layout không bị lệch
                                if (rowSkills.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    TextButton(onClick = onNavigateToMySkill, modifier = Modifier.fillMaxWidth()) {
                        Text("Xem tất cả kỹ năng →", color = Color(0xFF1976D2), fontSize = 13.sp)
                    }
                }
            }

            // --- 5. TÀI KHOẢN ---
            item {
                ProfileSection(title = "Tài khoản") {
                    SettingRow(
                        icon = Icons.Default.History,
                        text = "Lịch sử giao dịch",
                        onClick = onNavigateToHistory
                    )
                    SettingRow(Icons.Default.Security, "Bảo mật tài khoản")
                    SettingRow(
                        icon = Icons.Default.Logout,
                        text = "Đăng xuất hệ thống",
                        textColor = Color.Red,
                        onClick = {
                            sessionManager.logout()
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}

// --- CÁC COMPONENT PHỤ (Giữ nguyên) ---
@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SmallSkillItem(name: String, rating: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF0F7FF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(rating, color = Color(0xFFFFA000), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingRow(icon: ImageVector, text: String, textColor: Color = Color.Black, onClick: () -> Unit = {}) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (textColor == Color.Red) Color.Red else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, color = textColor, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}