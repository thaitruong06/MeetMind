package com.team7.meetmind.ui.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.components.VoiceWaveformHero
import com.team7.meetmind.ui.theme.InkSurfaceElevated
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalCoral
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary
import kotlinx.coroutines.delay

/**
 * Màn hình ghi âm trực tiếp — chưa nối AudioRecord/Foreground Service thật, chỉ dựng UI và
 * mô phỏng đồng hồ đếm giờ để xem trước trải nghiệm. Khi làm Giai đoạn 4, thay state cục bộ này
 * bằng dữ liệu thật từ RecordingViewModel (thời lượng, dòng phiên âm tạm thời từ STT streaming).
 */
@Composable
fun RecordingScreen(onStop: () -> Unit, onBack: () -> Unit) {
    var isPaused by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isPaused) {
        while (!isPaused) {
            delay(1000)
            elapsedSeconds++
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.Close, contentDescription = "Đóng", tint = TextMuted)
                }
                Text(
                    if (isPaused) "Đã tạm dừng" else "Đang ghi âm",
                    color = if (isPaused) TextMuted else SignalAmber,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.width(48.dp))
            }
            Spacer(Modifier.height(36.dp))
            Text(formatDuration(elapsedSeconds), style = MaterialTheme.typography.displaySmall, color = TextPrimary)
            Spacer(Modifier.height(28.dp))
            VoiceWaveformHero(modifier = Modifier.height(130.dp), barCount = 34)
            Spacer(Modifier.height(24.dp))
            Text(
                "“...vậy tuần sau mình demo thử màn hình đăng nhập...”",
                color = TextMuted,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                CircleActionButton(
                    icon = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    label = if (isPaused) "Tiếp tục" else "Tạm dừng",
                    isPrimary = false
                ) { isPaused = !isPaused }
                CircleActionButton(icon = Icons.Filled.Stop, label = "Dừng", isPrimary = true) { onStop() }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CircleActionButton(icon: ImageVector, label: String, isPrimary: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isPrimary) SignalCoral else InkSurfaceElevated)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isPrimary) Color.White else TextPrimary)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
