package com.team7.meetmind.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.presentation.auth.ForgotPasswordScreen
import com.team7.meetmind.presentation.auth.LoginScreen
import com.team7.meetmind.presentation.auth.RegisterScreen
import com.team7.meetmind.presentation.calendar.CalendarScreen
import com.team7.meetmind.presentation.folders.FoldersScreen
import com.team7.meetmind.presentation.home.HomeScreen
import com.team7.meetmind.presentation.meetingdetail.MeetingDetailScreen
import com.team7.meetmind.presentation.recording.RecordingScreen
import com.team7.meetmind.presentation.search.SearchScreen
import com.team7.meetmind.presentation.session.SessionViewModel
import com.team7.meetmind.presentation.session.SplashScreen
import com.team7.meetmind.presentation.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Gốc của giao diện. Điều hướng giữa "đã đăng nhập" và "chưa đăng nhập" hoàn toàn do trạng thái
 * phiên (SessionViewModel) quyết định, nên đăng nhập, đăng xuất, đặt lại mật khẩu hay phiên hết hạn
 * đều tự đưa người dùng tới đúng màn hình mà không màn hình nào phải tự điều hướng.
 */
@Composable
fun MeetMindRoot(sessionViewModel: SessionViewModel = koinViewModel()) {
    val session by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    when (session) {
        SessionState.Loading -> SplashScreen()
        else -> AppNavigation(session)
    }
}

@Composable
private fun AppNavigation(session: SessionState) {
    val navController = rememberNavController()
    // Chỉ chốt màn hình khởi đầu một lần; các thay đổi sau đó do LaunchedEffect bên dưới xử lý.
    val startDestination = remember { if (session is SessionState.Authenticated) Routes.HOME else Routes.LOGIN }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(session, currentRoute) {
        when {
            session is SessionState.Authenticated && currentRoute in Routes.AUTH_ROUTES ->
                navController.navigate(Routes.HOME) { popUpTo(navController.graph.id) { inclusive = true } }

            session is SessionState.Unauthenticated && currentRoute != null && currentRoute !in Routes.AUTH_ROUTES ->
                navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
        }
    }

    MeetMindNavGraph(navController = navController, startDestination = startDestination)
}

@Composable
private fun MeetMindNavGraph(navController: NavHostController, startDestination: String) {
    // Điều hướng giữa 4 tab chính: quay về Trang chủ khi bấm Back, giữ lại trạng thái từng tab.
    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ---------- Xác thực ----------
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }

        // ---------- 4 tab chính ----------
        composable(Routes.HOME) {
            HomeScreen(
                onMeetingClick = { id -> navController.navigate(Routes.meetingDetail(id)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) },
                onRecordClick = { navController.navigate(Routes.RECORDING) },
                onNavigate = navigateToTab
            )
        }
        composable(Routes.FOLDERS) {
            FoldersScreen(
                onNavigate = navigateToTab,
                onRecordClick = { navController.navigate(Routes.RECORDING) },
                onFolderClick = { /* TODO: lọc danh sách cuộc họp theo thư mục */ }
            )
        }
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onNavigate = navigateToTab,
                onRecordClick = { navController.navigate(Routes.RECORDING) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigate = navigateToTab,
                onRecordClick = { navController.navigate(Routes.RECORDING) }
            )
        }

        // ---------- Toàn màn hình, không có thanh điều hướng dưới ----------
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
            val meetingId = backStackEntry.arguments?.getString("meetingId").orEmpty()
            MeetingDetailScreen(meetingId = meetingId, onBack = { navController.popBackStack() })
        }
    }
}
