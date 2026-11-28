package com.team7.meetmind.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
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

private val MeetMindLightColors = androidx.compose.material3.lightColorScheme(
    primary = Color(0xFF9A5700), onPrimary = Color.White,
    secondary = Color(0xFFB34320), onSecondary = Color.White,
    tertiary = Color(0xFF6554A4), onTertiary = Color.White,
    background = Color(0xFFFFFBFF), onBackground = Color(0xFF1C1B20),
    surface = Color(0xFFFFFBFF), onSurface = Color(0xFF1C1B20),
    surfaceVariant = Color(0xFFF0EDF4), onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79757F), error = Color(0xFFBA1A1A), onError = Color.White
)

@Composable
fun MeetMindTheme(mode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val dark = when (mode) { ThemeMode.SYSTEM -> isSystemInDarkTheme(); ThemeMode.LIGHT -> false; ThemeMode.DARK -> true }
    val colors = if (dark) MeetMindDarkColors else MeetMindLightColors
    MeetMindPalette.update(dark)
    MaterialTheme(
        colorScheme = colors,
        typography = MeetMindTypography,
        shapes = MeetMindShapes,
        content = content
    )
}
