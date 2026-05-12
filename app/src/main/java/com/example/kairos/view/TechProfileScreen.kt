package com.example.kairos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TeacherProfileScreen(teacherId: Int, onBack: () -> Unit) {
    // Giả lập dữ liệu, mai chúng ta làm API fetch_user_profile.php sẽ đổ data thật vào
    val teacherName = "Nguyễn Văn Tin"
    val bio = "Sinh viên CNTT tại VKU. Chuyên lập trình Android và PHP."
    val rating = 4.8
    val totalLessons = 15

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Text("← Quay lại", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar giả lập
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color.Black
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(teacherName.first().toString(), color = Color.White, fontSize = 40.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(teacherName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("ID Người dùng: #$teacherId", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Chỉ số uy tín
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$rating ⭐", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Đánh giá", color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$totalLessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Bài đã dạy", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Giới thiệu", fontWeight = FontWeight.Bold)
                Text(bio, color = Color.DarkGray, fontSize = 14.sp)
            }
        }
    }
}