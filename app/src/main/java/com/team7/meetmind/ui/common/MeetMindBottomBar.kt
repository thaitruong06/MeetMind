package com.team7.meetmind.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team7.meetmind.navigation.Routes
import com.team7.meetmind.ui.auth.components.clickableNoRipple
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalCoral
import com.team7.meetmind.ui.theme.TextMuted

/**
 * Thanh điều hướng dưới dùng chung cho 4 tab chính (Trang chủ, Thư mục, Lịch, Cài đặt),
 * với nút "Ghi âm" nổi lên ở giữa — nơi duy nhất dùng gradient đậm trên thanh này, đúng chủ đích:
 * ghi âm là hành động người dùng sẽ bấm nhiều nhất trong app.
 */
@Composable
fun MeetMindBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(color = InkSurface, shadowElevation = 12.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavIcon(Icons.Filled.Home, "Trang chủ", currentRoute == Routes.HOME) { onNavigate(Routes.HOME) }
                BottomNavIcon(Icons.Filled.Folder, "Thư mục", currentRoute == Routes.FOLDERS) { onNavigate(Routes.FOLDERS) }
                Spacer(modifier = Modifier.weight(1f))
                BottomNavIcon(Icons.Filled.CalendarMonth, "Lịch", currentRoute == Routes.CALENDAR) { onNavigate(Routes.CALENDAR) }
                BottomNavIcon(Icons.Filled.Settings, "Cài đặt", currentRoute == Routes.SETTINGS) { onNavigate(Routes.SETTINGS) }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .size(60.dp)
                .shadow(10.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(SignalAmber, SignalCoral)))
                .clickable(onClick = onRecordClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Mic, contentDescription = "Ghi âm", tint = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
private fun RowScope.BottomNavIcon(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickableNoRipple(onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (selected) SignalAmber else TextMuted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
            color = if (selected) SignalAmber else TextMuted
        )
    }
}
