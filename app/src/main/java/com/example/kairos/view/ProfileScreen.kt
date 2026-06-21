package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.RetrofitClient
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToMySkill: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val profileData by profileViewModel.userProfile.collectAsState()
    val mySkills by profileViewModel.mySkills.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            profileViewModel.loadProfile(userId)
            profileViewModel.loadMySkills(userId)
        }
    }

    if (isLoading && profileData == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFf5eedc)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF14213d))
        }
        return
    }

    profileData?.let { user ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFf5eedc)), // Màu nền Cream chuẩn
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // --- 1. THẺ THÔNG TIN TÀI KHOẢN & VÍ (Giao diện Dark Blue mới) ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14213d)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Hàng Avatar và Tên
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2a3b5c)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.first().toString().uppercase(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = user.email,
                                    color = Color.LightGray,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Khung Ví KAIROS bên trong
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2a3b5c), RoundedCornerShape(16.dp))
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Ví KAIROS", color = Color.LightGray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${user.diamondBalance} xu", // Đổi icon 💎 thành chữ 'xu' theo ảnh
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Button(
                                    onClick = onNavigateToDeposit,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20c997)), // Màu nút ngọc Teal
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text("Nạp tiền", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. GIỚI THIỆU ---
            item {
                ProfileSection(title = "Giới thiệu") {
                    Text(
                        text = user.bio ?: "Chưa có thông tin giới thiệu.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            // --- 3. KỸ NĂNG CỦA TÔI ---
            item {
                ProfileSection(title = "Kỹ năng của tôi") {
                    if (mySkills.isEmpty()) {
                        Text("Bạn chưa đăng kỹ năng nào.", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        mySkills.chunked(2).forEach { rowSkills ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowSkills.forEach { skill ->
                                    SmallSkillItem(
                                        name = skill.title,
                                        rating = "${String.format("%.1f", skill.avgRating)} ★",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Cân bằng UI nếu mảng lẻ
                                if (rowSkills.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Xem tất cả kỹ năng",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToMySkill() }
                            .padding(vertical = 8.dp)
                    )
                }
            }

            // --- 4. TÀI KHOẢN ---
            item {
                ProfileSection(title = "Tài khoản") {
                    SettingRow(
                        icon = Icons.Default.History,
                        text = "Lịch sử giao dịch",
                        onClick = onNavigateToHistory
                    )
                    SettingRow(
                        icon = Icons.Default.Security,
                        text = "Hồ sơ cá nhân",
                        onClick = onNavigateToEditProfile
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingRow(
                        icon = Icons.Default.Logout,
                        text = "Đăng xuất",
                        textColor = Color.Red,
                        iconColor = Color.Red,
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

// --- CÁC COMPONENT PHỤ (Đã cấu trúc lại UI) ---

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp), // Bo góc tròn to theo bản thiết kế
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SmallSkillItem(name: String, rating: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFf5eedc), shape = RoundedCornerShape(16.dp)) // Màu nền cream
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rating,
                color = Color(0xFF20c997), // Đổi màu đánh giá sang Teal
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingRow(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (textColor == Color.Red) Color.Red else Color.Gray
        )
    }
}