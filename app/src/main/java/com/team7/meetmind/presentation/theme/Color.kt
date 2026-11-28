package com.team7.meetmind.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal object MeetMindPalette {
    private var dark by mutableStateOf(true)
    fun update(isDark: Boolean) { dark = isDark }
    val background get() = if (dark) Color(0xFF15141C) else Color(0xFFFFFBFF)
    val surface get() = if (dark) Color(0xFF1E1C28) else Color(0xFFF5F2F8)
    val elevated get() = if (dark) Color(0xFF262333) else Color(0xFFECE8F0)
    val outline get() = if (dark) Color(0xFF34303F) else Color(0xFF79757F)
    val primaryText get() = if (dark) Color(0xFFF4F1EC) else Color(0xFF1C1B20)
    val muted get() = if (dark) Color(0xFFB8B4C2) else Color(0xFF49454F)
    val faint get() = if (dark) Color(0xFF9B97A6) else Color(0xFF625D68)
    val amber get() = if (dark) Color(0xFFF2A33C) else Color(0xFF9A5700)
    val coral get() = if (dark) Color(0xFFF2683C) else Color(0xFFB34320)
    val violet get() = if (dark) Color(0xFF8C7AE6) else Color(0xFF6554A4)
    val success get() = if (dark) Color(0xFF4FD1A5) else Color(0xFF006B52)
    val error get() = if (dark) Color(0xFFFF6B6B) else Color(0xFFBA1A1A)
}

// Nền ấm, tối (không dùng đen thuần) — gợi cảm giác phòng họp buổi tối, tĩnh lặng để tập trung lắng nghe
val InkBackground get() = MeetMindPalette.background
val InkSurface get() = MeetMindPalette.surface
val InkSurfaceElevated get() = MeetMindPalette.elevated
val InkOutline get() = MeetMindPalette.outline

// Cặp màu "tín hiệu" lấy cảm hứng từ sóng âm khi ghi âm cuộc họp — chỉ dùng cho điểm nhấn
// (waveform, nút chính, liên kết), không trải đều khắp giao diện
val SignalAmber get() = MeetMindPalette.amber
val SignalCoral get() = MeetMindPalette.coral
val SignalViolet get() = MeetMindPalette.violet

val TextPrimary get() = MeetMindPalette.primaryText
val TextMuted get() = MeetMindPalette.muted
val TextFaint get() = MeetMindPalette.faint

val StateSuccess get() = MeetMindPalette.success
val StateError get() = MeetMindPalette.error
