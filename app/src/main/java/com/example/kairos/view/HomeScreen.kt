package com.example.kairos.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable // Thêm thư viện để làm nút bấm
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.network.Skill
import com.example.kairos.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToCreateSkill: () -> Unit, // Nút để nhảy sang trang Đăng bài
    onNavigateToMyBookings: () -> Unit,
    onNavigateToSkillDetail: (Skill) -> Unit // Lệnh chuyển sang trang chi tiết
) {
    val skills by homeViewModel.skillList.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    // Tự động tải dữ liệu khi vừa mở màn hình này lên
    LaunchedEffect(Unit) {
        homeViewModel.loadSkills()
    }

    Scaffold(
        floatingActionButton = {
            // Nút bấm nổi màu đen góc phải dưới để Đăng bài nhanh
            FloatingActionButton(
                onClick = { onNavigateToCreateSkill() },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+", fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Header trang chủ
            Text("Khám phá", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Tìm kiếm kỹ năng bạn muốn học hôm nay", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else if (skills.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có bài đăng nào. Hãy là người đầu tiên!", color = Color.Gray)
                }
            } else {
                // Danh sách kỹ năng (Cuộn mượt mà)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Tránh bị cái nút Floating che mất
                ) {
                    items(skills) { skill ->
                        // Truyền lệnh click xuống cho từng cái thẻ
                        SkillCard(
                            skill = skill,
                            onClick = onNavigateToSkillDetail
                        )
                    }
                }
            }
        }
    }
}

// Thiết kế 1 cái thẻ (Card) tối giản
@Composable
fun SkillCard(skill: Skill, onClick: (Skill) -> Unit) { // Thêm tham số onClick ở đây
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(skill) }, // Gắn hiệu ứng bấm được vào Card
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // Viền xám mỏng
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Tiêu đề
            Text(skill.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả (Cắt gọn nếu quá dài)
            Text(
                text = skill.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tác giả và Giá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Bởi: ${skill.author_name}", fontSize = 12.sp, color = Color.Gray)

                // Hiển thị giá kim cương nổi bật
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Text(
                        text = "${skill.price_diamonds} 💎",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}