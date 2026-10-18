package com.team7.meetmind.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Giao diện luôn ở chế độ tối theo chủ đích (phù hợp bối cảnh nghe lại/ghi âm cuộc họp,
// đỡ chói khi dùng buổi tối), không chuyển theo theme hệ thống.
private val MeetMindDarkColors = darkColorScheme(
    primary = SignalAmber,
    onPrimary = Color(0xFF231405),
    secondary = SignalCoral,
    onSecondary = Color(0xFF2B0E04),
    tertiary = SignalViolet,
    onTertiary = Color(0xFF1C1430),
    background = InkBackground,
    onBackground = TextPrimary,
    surface = InkSurface,
    onSurface = TextPrimary,
    surfaceVariant = InkSurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = InkOutline,
    error = StateError,
    onError = Color.White
)

@Composable
fun MeetMindTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MeetMindDarkColors,
        typography = MeetMindTypography,
        shapes = MeetMindShapes,
        content = content
    )
}
