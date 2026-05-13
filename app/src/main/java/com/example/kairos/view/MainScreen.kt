package com.example.kairos.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kairos.model.SharedData

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Trang chủ", Icons.Default.Home)
    object MyBookings : Screen("my_bookings", "Lịch sử", Icons.Default.List)
    object Profile : Screen("profile", "Cá nhân", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.MyBookings.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    val items = listOf(Screen.Home, Screen.MyBookings, Screen.Profile)
                    items.forEach { screen ->
                        NavigationBarItem(
                            // 2. Sử dụng thẻ Icon thay vì Text
                            icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.Black,
                                indicatorColor = Color.Black, // Màu nền đen khi được chọn
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "login") {

                // Giai đoạn 1: Login/Register
                composable("login") {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }

                // Giai đoạn 2: Home
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToCreateSkill = { navController.navigate("create_skill") },
                        onNavigateToMyBookings = { navController.navigate(Screen.MyBookings.route) },
                        onNavigateToSkillDetail = { skill ->
                            SharedData.selectedSkill = skill
                            navController.navigate("skill_detail")
                        }
                    )
                }

                // Giai đoạn 3: Lịch sử học
                composable(Screen.MyBookings.route) {
                    HistoryScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToChat = { txnId -> /* Giai đoạn 4 */ }
                    )
                }

                // Giai đoạn 3: Profile
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToMySales = {
                            navController.navigate("my_sales")
                        }
                    )
                }

                // Các trang phụ
                composable("create_skill") {
                    CreateSkillScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable("skill_detail") {
                    val skill = SharedData.selectedSkill
                    if (skill != null) {
                        SkillDetailScreen(
                            skill = skill,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}