package com.example.kairos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kairos.network.SharedData
import com.example.kairos.view.CreateSkillScreen
import com.example.kairos.view.HomeScreen
import com.example.kairos.view.LoginScreen
import com.example.kairos.view.MyBookingsScreen
import com.example.kairos.view.RegisterScreen
import com.example.kairos.view.SkillDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController() // Khởi tạo bộ điều khiển

            // Thiết lập các tuyến đường
            NavHost(navController = navController, startDestination = "login") {
                // Tuyến đường Đăng nhập
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = {
                            navController.navigate("register")
                        },
                        onNavigateToHome = { // THÊM XỬ LÝ CHUYỂN TRANG VÀO TRANG CHỦ
                            navController.navigate("home") {
                                // Xóa trang Login khỏi lịch sử điều hướng
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // Tuyến đường Đăng ký
                composable("register") {
                    RegisterScreen(onNavigateToLogin = {
                        navController.popBackStack()
                    })
                }
                // Thêm vào trong khối NavHost của MainActivity
                composable("create_skill") {
                    CreateSkillScreen(onNavigateBack = {
                        navController.popBackStack()
                    })
                }
                composable("home") {
                    HomeScreen(
                        onNavigateToCreateSkill = { navController.navigate("create_skill") },
                        onNavigateToMyBookings = { navController.navigate("my_bookings") },
                        // Xử lý khi bấm vào 1 bài kỹ năng
                        onNavigateToSkillDetail = { clickedSkill ->
                            SharedData.selectedSkill = clickedSkill // Cất vào khay
                            navController.navigate("skill_detail")  // Chuyển trang
                        }
                    )
                }
                composable("skill_detail") {
                    val skill = SharedData.selectedSkill // Lấy từ khay ra
                    if (skill != null) {
                        SkillDetailScreen(
                            skill = skill,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("my_bookings") {
                    MyBookingsScreen(
                        onBack = {
                            navController.popBackStack() // Lệnh để quay lại trang trước
                        }
                    )
                }
            }
        }
    }
}