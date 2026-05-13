package com.example.kairos.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kairos.model.SharedData
import com.example.kairos.network.SessionManager
import com.example.kairos.viewmodel.SkillState
import com.example.kairos.viewmodel.SkillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSkillScreen(
    onNavigateBack: () -> Unit,
    skillViewModel: SkillViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    // Lấy data từ SharedData
    val skillToEdit = SharedData.selectedSkill

    // Gán dữ liệu cũ vào
    var title by remember { mutableStateOf(skillToEdit?.title ?: "") }
    var description by remember { mutableStateOf(skillToEdit?.description ?: "") }
    var priceStr by remember { mutableStateOf(skillToEdit?.priceDiamonds?.toString() ?: "") }

    val skillState by skillViewModel.skillState.collectAsState()

    // Dọn rác khi thoát màn hình Edit
    DisposableEffect(Unit) {
        onDispose { SharedData.selectedSkill = null }
    }

    LaunchedEffect(skillState) {
        when (skillState) {
            is SkillState.Success -> {
                Toast.makeText(context, (skillState as SkillState.Success).message, Toast.LENGTH_SHORT).show()
                skillViewModel.resetState()
                onNavigateBack()
            }
            is SkillState.Error -> {
                Toast.makeText(context, (skillState as SkillState.Error).message, Toast.LENGTH_SHORT).show()
                skillViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sửa bài đăng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tên kỹ năng") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả chi tiết") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 5)
            OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Giá (💎)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val price = priceStr.toIntOrNull() ?: 0
                    if (title.isBlank() || description.isBlank() || price <= 0 || skillToEdit == null) {
                        Toast.makeText(context, "Vui lòng nhập đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Chỉ gọi Edit
                    skillViewModel.editSkill(skillToEdit.skillId, userId, title, description, price)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = skillState !is SkillState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                if (skillState is SkillState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Cập nhật", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}