package com.example.kairos.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kairos.model.SharedData

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home    : Screen("home",    "Trang chủ", Icons.Default.Home)
    object History : Screen("history", "Lịch sử",   Icons.AutoMirrored.Filled.List)
    object Profile : Screen("profile", "Cá nhân",   Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    listOf(Screen.Home, Screen.History, Screen.Profile).forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Color.White,
                                selectedTextColor   = Color.Black,
                                indicatorColor      = Color.Black,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState    = true
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

                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToCreateSkill = { navController.navigate("create_skill") },
                        onNavigateToHistory     = { navController.navigate(Screen.History.route) },
                        onNavigateToSkillDetail = { skill ->
                            SharedData.selectedSkill = skill
                            navController.navigate("skill_detail")
                        },
                        onNavigateToNotifications = { navController.navigate("notifications") }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToChat = { txnId, partnerName ->
                            val encodedName = java.net.URLEncoder.encode(partnerName, "UTF-8")
                            navController.navigate("chat/$txnId/$encodedName")
                        },
                        onNavigateToReport = { txnId, reportedUserId ->
                            navController.navigate("report_screen/$txnId/$reportedUserId")
                        }
                    )
                }

                composable(
                    route = "chat/{transactionId}/{partnerName}",
                    arguments = listOf(
                        navArgument("transactionId") { type = NavType.IntType },
                        navArgument("partnerName")   { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val txnId       = backStackEntry.arguments?.getInt("transactionId") ?: return@composable
                    val partnerName = backStackEntry.arguments?.getString("partnerName")
                        ?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: "Người dùng"
                    ChatScreen(
                        transactionId  = txnId,
                        partnerName    = partnerName,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "report_screen/{transactionId}/{reportedUser}",
                    arguments = listOf(
                        navArgument("transactionId") { type = NavType.IntType },
                        navArgument("reportedUser") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                    val reportedUser = backStackEntry.arguments?.getInt("reportedUser") ?: 0

                    ReportScreen(
                        transactionId = transactionId,
                        reportedUser = reportedUser,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToMySkill = { navController.navigate("my_skills") },
                        onNavigateToHistory = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onNavigateToDeposit = { navController.navigate("deposit_screen") },
                        onNavigateToEditProfile = {navController.navigate("edit_profile")}
                    )
                }

                composable("create_skill") {
                    CreateSkillScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable("skill_detail") {
                    val skill = SharedData.selectedSkill
                    if (skill != null) {
                        SkillDetailScreen(
                            skill                      = skill,
                            onNavigateBack             = { navController.popBackStack() },
                            onNavigateToTeacherProfile = { teacherId ->
                                navController.navigate("teacher_profile/$teacherId")
                            }
                        )
                    }
                }

                composable("my_skills") {
                    MySkillScreen(
                        onNavigateBack     = { navController.popBackStack() },
                        onNavigateToEdit   = { skill ->
                            SharedData.selectedSkill = skill
                            navController.navigate("edit_skill")
                        },
                        onNavigateToCreate = { navController.navigate("create_skill") }
                    )
                }

                composable("edit_skill") {
                    EditSkillScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(
                    route = "teacher_profile/{teacherId}",
                    arguments = listOf(
                        navArgument("teacherId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val teacherId = backStackEntry.arguments?.getInt("teacherId") ?: return@composable
                    TeacherProfileScreen(
                        teacherId               = teacherId,
                        onBack                  = { navController.popBackStack() },
                        onNavigateToSkillDetail = { skill ->
                            SharedData.selectedSkill = skill
                            navController.navigate("skill_detail")
                        }
                    )
                }
                composable("deposit_screen") {
                    DepositScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable("edit_profile") {
                    EditProfileScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("notifications") {
                    NotificationScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}