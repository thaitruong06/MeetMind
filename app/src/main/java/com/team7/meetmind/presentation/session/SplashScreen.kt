package com.team7.meetmind.presentation.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.team7.meetmind.presentation.auth.components.VoiceWaveformHero
import com.team7.meetmind.presentation.theme.TextMuted
import com.team7.meetmind.presentation.theme.TextPrimary

/** Hiển thị trong lúc app đọc phiên đăng nhập đã lưu (thường chỉ vài trăm mili giây). */
@Composable
fun SplashScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VoiceWaveformHero()
            Spacer(Modifier.height(24.dp))
            Text("MeetMind", style = MaterialTheme.typography.displaySmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Trợ lý họp AI", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
        }
    }
}
