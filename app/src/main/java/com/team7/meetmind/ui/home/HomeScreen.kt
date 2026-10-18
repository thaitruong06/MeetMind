package com.team7.meetmind.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.AuthViewModel
import com.team7.meetmind.ui.auth.components.GradientAuthButton
import com.team7.meetmind.ui.auth.components.VoiceWaveformHero
import com.team7.meetmind.ui.theme.TextMuted

/**
 * Placeholder sau khi đăng nhập — chỉ để chứng minh luồng xác thực đầy đủ hoạt động.
 * Các tính năng chính (ghi âm, phiên âm, tóm tắt...) sẽ được xây ở giai đoạn 4 của timeline.
 */
@Composable
fun HomeScreen(viewModel: AuthViewModel, onLoggedOut: () -> Unit) {
    val user by viewModel.currentUser.collectAsState()

    LaunchedEffect(user) {
        if (user == null) onLoggedOut()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(48.dp))
            VoiceWaveformHero()
            Spacer(Modifier.height(28.dp))
            Text(
                "Xin chào, ${user?.displayName?.ifBlank { null } ?: user?.email ?: "bạn"}",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Bạn đã đăng nhập thành công vào MeetMind.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMuted
            )
            Spacer(Modifier.height(40.dp))
            GradientAuthButton(
                text = "Đăng xuất",
                onClick = { viewModel.logout() }
            )
        }
    }
}
