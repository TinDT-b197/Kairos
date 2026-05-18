package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.model.Skill
import com.example.kairos.viewmodel.TeacherProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(
    teacherId: Int,
    onBack: () -> Unit,
    onNavigateToSkillDetail: (Skill) -> Unit,
    viewModel: TeacherProfileViewModel = viewModel()
) {
    val profile by viewModel.teacherProfile.collectAsState()
    val skills by viewModel.teacherSkills.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(teacherId) {
        viewModel.loadTeacherData(teacherId)
    }

    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ giảng viên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F9FF))
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF1976D2)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("😕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            errorMessage ?: "Có lỗi xảy ra",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadTeacherData(teacherId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) { Text("Thử lại") }
                    }
                }
                profile != null -> {
                    val user = profile!!

                    // Tính rating trung bình từ danh sách kỹ năng
                    val totalReviews = skills.sumOf { it.totalReviews }
                    val totalLearners = skills.sumOf { it.totalLearners }

                    // CHỈ LỌC NHỮNG KỸ NĂNG ĐÃ CÓ ĐÁNH GIÁ để tính trung bình
                    val ratedSkills = skills.filter { it.totalReviews > 0 }
                    val avgRating = if (ratedSkills.isNotEmpty()) {
                        ratedSkills.map { it.avgRating }.average()
                    } else {
                        0.0
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // --- HEADER GRADIENT ---
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .background(blueGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Avatar chữ cái đầu
                                    Surface(
                                        modifier = Modifier.size(85.dp),
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.25f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = user.name.first().uppercase(),
                                                fontSize = 34.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        user.name,
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        user.email,
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // --- STATS CARD ---
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .offset(y = (-24).dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TeacherStatItem(
                                        value = skills.size.toString(),
                                        label = "Kỹ năng"
                                    )
                                    VerticalDivider(
                                        modifier = Modifier.height(40.dp),
                                        color = Color(0xFFE0E0E0)
                                    )
                                    TeacherStatItem(
                                        value = if (totalReviews > 0) String.format("%.1f", avgRating) else "Mới",
                                        label = "Đánh giá",
                                        icon = if (totalReviews > 0) "⭐" else "✨"
                                    )
                                    VerticalDivider(
                                        modifier = Modifier.height(40.dp),
                                        color = Color(0xFFE0E0E0)
                                    )
                                    TeacherStatItem(
                                        value = totalLearners.toString(),
                                        label = "Lượt học"
                                    )
                                }
                            }
                        }

                        // --- BIO ---
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Giới thiệu",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = user.bio ?: "Giảng viên chưa cập nhật thông tin giới thiệu.",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 21.sp
                                    )
                                }
                            }
                        }

                        // --- DANH SÁCH KỸ NĂNG ---
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Kỹ năng đang chia sẻ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFE3F2FD)
                                ) {
                                    Text(
                                        "${skills.size} kỹ năng",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 12.sp,
                                        color = Color(0xFF1976D2),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (skills.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Chưa có kỹ năng nào được đăng.",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            items(skills) { skill ->
                                TeacherSkillCard(
                                    skill = skill,
                                    onClick = { onNavigateToSkillDetail(skill) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherStatItem(value: String, label: String, icon: String = "") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$icon$value",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0D47A1)
        )
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun TeacherSkillCard(skill: Skill, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon skill
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📚", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (skill.totalReviews == 0) {
                        Text(
                            text = "Khóa mới",
                            color = Color(0xFFE91E63),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        // NẾU ĐÃ CÓ ĐÁNH GIÁ -> Vẽ sao vàng bình thường
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", skill.avgRating),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${skill.totalReviews} đánh giá)",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                // Giá
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${skill.priceDiamonds}💎",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D47A1)
                    )
                }
            }
        }
    }
}