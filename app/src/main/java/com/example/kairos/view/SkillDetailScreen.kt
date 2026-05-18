package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.model.Skill
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.SkillDetailViewModel
import com.example.kairos.viewmodel.TransactionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    skill: Skill,
    viewModel: SkillDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTeacherProfile: (Int) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val buyerId = sessionManager.getUserId()

    val txnState by viewModel.txnState.collectAsState()

    LaunchedEffect(txnState) {
        if (txnState is TransactionState.Success) {
            Toast.makeText(context, (txnState as TransactionState.Success).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
            onNavigateBack()
        } else if (txnState is TransactionState.Error) {
            Toast.makeText(context, (txnState as TransactionState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết kỹ năng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // --- TÊN KỸ NĂNG ---
                Text(
                    text = skill.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- CARD THÔNG TIN TÁC GIẢ (có thể click) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Chỉ cho xem profile nếu không phải chính mình
                                if (buyerId != skill.sellerId) {
                                    onNavigateToTeacherProfile(skill.sellerId)
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = skill.authorName.firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = skill.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (buyerId == skill.sellerId) "Bài đăng của bạn"
                                else "Xem hồ sơ →",
                                fontSize = 12.sp,
                                color = if (buyerId == skill.sellerId) Color.Gray
                                else Color(0xFF1976D2)
                            )
                        }
                        // Rating
                        if (skill.totalReviews == 0) {
                            Text(
                                text = "Chưa có đánh giá nào",
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
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- MÔ TẢ ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Mô tả chi tiết", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = skill.description,
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 23.sp
                        )
                    }
                }
            }

            // --- THANH TOÁN (đáy cố định) ---
            Surface(
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Giá kỹ năng", color = Color.Gray, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "${skill.priceDiamonds}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0D47A1)
                            )
                            Text(" 💎", fontSize = 18.sp, modifier = Modifier.padding(bottom = 3.dp))
                        }
                    }

                    if (buyerId == skill.sellerId) {
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                            enabled = false,
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Kỹ năng của bạn", color = Color.Gray)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.bookSkill(
                                    buyerId = buyerId,
                                    sellerId = skill.sellerId,
                                    skillId = skill.skillId,
                                    price = skill.priceDiamonds
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            enabled = txnState !is TransactionState.Loading,
                            modifier = Modifier.height(50.dp)
                        ) {
                            if (txnState is TransactionState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Đăng ký ngay",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}