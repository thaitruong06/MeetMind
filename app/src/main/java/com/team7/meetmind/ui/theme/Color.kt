package com.team7.meetmind.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
internal data class AdditionalColors(
    val textFaint: Color,
    val success: Color,
    val onSuccess: Color,
    val disabledContainer: Color,
    val onDisabled: Color
)

internal val LocalAdditionalColors = staticCompositionLocalOf<AdditionalColors> {
    error("Colors must be read inside MeetMindTheme")
}

// Keep existing screen tokens, but resolve them from the active theme on every read.
val InkBackground: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
val InkSurface: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
val InkSurfaceElevated: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceContainerHigh
val InkOutline: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
val SignalAmber: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
val SignalCoral: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
val SignalViolet: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
val TextPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
val TextMuted: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
val TextFaint: Color @Composable @ReadOnlyComposable get() = LocalAdditionalColors.current.textFaint
val StateSuccess: Color @Composable @ReadOnlyComposable get() = LocalAdditionalColors.current.success
val OnSuccess: Color @Composable @ReadOnlyComposable get() = LocalAdditionalColors.current.onSuccess
val StateError: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error
val DisabledContainer: Color @Composable @ReadOnlyComposable get() = LocalAdditionalColors.current.disabledContainer
val OnDisabled: Color @Composable @ReadOnlyComposable get() = LocalAdditionalColors.current.onDisabled

enum class ThemeAccent { AMBER, CORAL, VIOLET, SUCCESS }

@Composable
@ReadOnlyComposable
fun ThemeAccent.color(): Color = when (this) {
    ThemeAccent.AMBER -> SignalAmber
    ThemeAccent.CORAL -> SignalCoral
    ThemeAccent.VIOLET -> SignalViolet
    ThemeAccent.SUCCESS -> StateSuccess
}
