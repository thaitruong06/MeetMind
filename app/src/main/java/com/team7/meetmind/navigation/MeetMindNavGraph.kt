package com.team7.meetmind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team7.meetmind.ui.auth.AuthViewModel
import com.team7.meetmind.ui.auth.ForgotPasswordScreen
import com.team7.meetmind.ui.auth.LoginScreen
import com.team7.meetmind.ui.auth.RegisterScreen
import com.team7.meetmind.ui.calendar.CalendarScreen
import com.team7.meetmind.ui.folders.FoldersScreen
import com.team7.meetmind.ui.home.HomeScreen
import com.team7.meetmind.ui.meetingdetail.MeetingDetailScreen
import com.team7.meetmind.ui.recording.RecordingScreen
import com.team7.meetmind.ui.search.SearchScreen
import com.team7.meetmind.ui.settings.SettingsScreen

@Composable
fun MeetMindNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val startDestination = if (authViewModel.currentUser.value != null) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        // ---------- Xác thực ----------
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(viewModel = authViewModel, onBackToLogin = { navController.popBackStack() })
        }

        // ---------- 4 tab chính (đều có MeetMindBottomBar riêng) ----------
        composable(Routes.HOME) {
            val user by authViewModel.currentUser.collectAsState()
            HomeScreen(
                userName = user?.displayName?.ifBlank { null } ?: user?.email ?: "bạn",
                onMeetingClick = { id -> navController.navigate(Routes.meetingDetail(id)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) },
                onRecordClick = { navController.navigate(Routes.RECORDING) },
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } }
            )
        }
        composable(Routes.FOLDERS) {
            FoldersScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onRecordClick = { navController.navigate(Routes.RECORDING) },
                onFolderClick = { /* TODO: lọc danh sách cuộc họp theo thư mục */ }
            )
        }
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onRecordClick = { navController.navigate(Routes.RECORDING) }
            )
        }
        composable(Routes.SETTINGS) {
            val user by authViewModel.currentUser.collectAsState()
            SettingsScreen(
                userLabel = user?.displayName?.ifBlank { null } ?: user?.email ?: "Tài khoản",
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
                onRecordClick = { navController.navigate(Routes.RECORDING) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ---------- Màn hình toàn màn hình, không có bottom bar ----------
        composable(Routes.RECORDING) {
            RecordingScreen(
                onStop = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onMeetingClick = { id -> navController.navigate(Routes.meetingDetail(id)) }
            )
        }
        composable(
            route = Routes.MEETING_DETAIL,
            arguments = listOf(navArgument("meetingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            MeetingDetailScreen(meetingId = meetingId, onBack = { navController.popBackStack() })
        }
    }
}
