package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.model.Skill
import com.example.kairos.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateSkill: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSkillDetail: (Skill) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    // 1. Theo dõi dữ liệu từ ViewModel
    val skillList by homeViewModel.skillList.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    // 2. Trạng thái tìm kiếm
    var searchQuery by remember { mutableStateOf("") }

    // 3. Logic lọc danh sách (Não bộ của chức năng Search)
    val filteredSkills = remember(searchQuery, skillList) {
        if (searchQuery.isEmpty()) {
            skillList
        } else {
            skillList.filter { skill ->
                // Tìm kiếm không phân biệt hoa thường theo tiêu đề hoặc tác giả
                skill.title.contains(searchQuery, ignoreCase = true) ||
                        skill.authorName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Tự động tải dữ liệu khi vào màn hình
    LaunchedEffect(Unit) {
        homeViewModel.loadSkills()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateSkill,
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm kỹ năng")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // --- PHẦN 1: THANH TÌM KIẾM ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(54.dp),
                    placeholder = { Text("Bạn đang cần kỹ năng gì?", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color(0xFFF0F0F0),
                        focusedContainerColor = Color(0xFFF9F9F9),
                        unfocusedContainerColor = Color(0xFFF9F9F9)
                    ),
                    singleLine = true
                )
            }

            // --- PHẦN 2: BANNER ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "KAIROS - Kết nối tri thức",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- PHẦN 3: GỢI Ý (VUỐT NGANG) ---
            // Phần này chúng ta lấy 5 bài đầu tiên từ danh sách GỐC (để luôn hiện gợi ý)
            item {
                Text("Gợi ý cho bạn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(skillList.take(5)) { skill ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onNavigateToSkillDetail(skill) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FlashOn,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (skill.title.length > 10) skill.title.take(8) + ".." else skill.title,
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- PHẦN 4: TẤT CẢ KỸ NĂNG (DÙNG DANH SÁCH LỌC) ---
            item {
                Text("Tất cả kỹ năng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                isLoading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }
                }
                errorMessage != null -> {
                    item {
                        Text(text = errorMessage ?: "Lỗi kết nối", color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
                filteredSkills.isEmpty() -> {
                    item {
                        Text(
                            "Không tìm thấy kỹ năng phù hợp.",
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    // Hiển thị danh sách thẻ ngang dựa trên kết quả tìm kiếm
                    items(filteredSkills) { skill ->
                        SkillHorizontalCard(
                            skill = skill,
                            onClick = { onNavigateToSkillDetail(skill) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SkillHorizontalCard(skill: Skill, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon đại diện bên trái
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Work,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung chính (Tiêu đề và Tác giả)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = skill.authorName,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }


            // Giá trị kim cương bên phải
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${skill.priceDiamonds}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(text = " 💎", fontSize = 12.sp)
            }
            if (skill.totalReviews == 0) {
                // Nếu chưa có ai đánh giá
                Text(
                    text = "Khóa mới",
                    color = Color(0xFFE91E63), // Màu hồng nổi bật
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                // Nếu đã có đánh giá
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp).padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", skill.avgRating),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = " (${skill.totalReviews})",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}