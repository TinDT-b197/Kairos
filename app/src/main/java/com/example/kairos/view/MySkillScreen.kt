package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.model.Skill
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.ProfileViewModel
import com.example.kairos.viewmodel.SkillState
import com.example.kairos.viewmodel.SkillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySkillScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Skill) -> Unit,
    onNavigateToCreate: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    skillViewModel: SkillViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    val mySkills by profileViewModel.mySkills.collectAsState()
    val skillState by skillViewModel.skillState.collectAsState()
    var skillToDelete by remember { mutableStateOf<Skill?>(null) }

    LaunchedEffect(Unit) {
        profileViewModel.loadMySkills(userId)
    }

    LaunchedEffect(skillState) {
        when (skillState) {
            is SkillState.Success -> {
                val message = (skillState as SkillState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                profileViewModel.loadMySkills(userId)
                skillViewModel.resetState()
            }
            is SkillState.Error -> {
                val error = (skillState as SkillState.Error).message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                skillViewModel.resetState()
            }
            else -> {}
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý bài đăng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm bài đăng mới")
            }
        }

    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F9FF))) {

            if (mySkills.isEmpty()) {
                Text(
                    text = "Bạn chưa chia sẻ kỹ năng nào.\nHãy tạo bài đăng mới để giao lưu nhé!",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mySkills) { skill ->
                        SkillManageItem(
                            skill = skill,
                            onEditClick = { onNavigateToEdit(skill) },
                            onDeleteClick = { skillToDelete = skill }
                        )
                    }
                }
            }

            if (skillState is SkillState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF1976D2)
                )
            }
        }

        skillToDelete?.let { skill ->
            AlertDialog(
                onDismissRequest = { skillToDelete = null },
                title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc chắn muốn xóa bài chia sẻ '${skill.title}' không? Hành động này sẽ không thể hoàn tác.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            skillViewModel.deleteSkill(skillId = skill.skillId, userId = userId)
                            skillToDelete = null
                        }
                    ) {
                        Text("Xóa ngay", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { skillToDelete = null }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun SkillManageItem(skill: Skill, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = skill.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${skill.priceDiamonds} 💎", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${String.format("%.1f", skill.avgRating)} ⭐", color = Color(0xFFFFA000), fontSize = 12.sp)
                }
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF1976D2))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                }
            }
        }
    }
}