package com.example.kairos.view

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.R
import com.example.kairos.model.Skill
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.HomeViewModel
import com.example.kairos.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateSkill: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSkillDetail: (Skill) -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel() // Khai báo ProfileViewModel giống trang Profile
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // 1. Theo dõi dữ liệu từ ViewModel của Home
    val skillList by homeViewModel.skillList.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()
    val profileData by profileViewModel.userProfile.collectAsState()

    // 2. Trạng thái tìm kiếm
    var searchQuery by remember { mutableStateOf("") }

    // Lấy tên người dùng từ API
    val userName = profileData?.name ?: "Học viên"

    // 3. Logic lọc danh sách
    val filteredSkills = remember(searchQuery, skillList) {
        if (searchQuery.isEmpty()) {
            skillList
        } else {
            skillList.filter { skill ->
                skill.title.contains(searchQuery, ignoreCase = true) ||
                        skill.authorName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadSkills()
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            profileViewModel.loadProfile(userId)
        }
    }
    Scaffold(
        containerColor = Color(0xFF14213d),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateSkill,
                containerColor = Color(0xFF14213d),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm kỹ năng")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2a3b5c)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2a3b5c), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Kairos",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.size(40.dp)
                    ) {
                        BadgedBox(
                            badge = { Badge(containerColor = Color.Red) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Thông báo",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Chào mừng, $userName!",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = "Nâng tầm kỹ năng - Đổi kim xu",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Thanh Tìm kiếm (Search Bar)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("Tìm kiếm kỹ năng...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFf5eedc), // Màu nền kem/trắng sữa
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
                ) {

                    // --- BANNER ---
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner),
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // --- KHÓA HỌC NỔI BẬT ---
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Khóa học nổi bật", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Xem tất cả >", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            // Lấy 5 khóa học đầu tiên làm nổi bật
                            items(skillList.take(5)) { skill ->
                                SkillVerticalCard(skill = skill, onClick = { onNavigateToSkillDetail(skill) })
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // --- KỸ NĂNG ĐỀ XUẤT ---
                    item {
                        Text("Kỹ năng đề xuất cho bạn", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    when {
                        isLoading -> {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF14213d))
                                }
                            }
                        }
                        errorMessage != null -> {
                            item { Text(text = errorMessage ?: "Lỗi kết nối", color = Color.Red) }
                        }
                        filteredSkills.isEmpty() -> {
                            item { Text("Không tìm thấy kỹ năng phù hợp.", color = Color.Gray) }
                        }
                        else -> {
                            items(filteredSkills) { skill ->
                                SkillHorizontalCard(skill = skill, onClick = { onNavigateToSkillDetail(skill) })
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Thẻ Khóa học Nổi bật
@Composable
fun SkillVerticalCard(skill: Skill, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Box màu xanh lá mờ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0xFF209c84), shape = RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text("Kỹ năng", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(skill.authorName, color = Color.White, fontSize = 10.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = skill.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${skill.priceDiamonds} xu",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

// Thẻ Kỹ năng đề xuất (Ngang - Vuốt dọc)
@Composable
fun SkillHorizontalCard(skill: Skill, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFe9ecef)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF14213d),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${skill.priceDiamonds} xu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }
    }
}