package com.team7.meetmind.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.team7.meetmind.R

/** Read values/values-night for the selected mode without changing the device configuration. */
internal fun loadThemeColors(context: Context, darkTheme: Boolean): Pair<ColorScheme, AdditionalColors> {
    val configuration = Configuration(context.resources.configuration).apply {
        uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
            if (darkTheme) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
    }
    val themedContext = context.createConfigurationContext(configuration)
    fun color(id: Int) = Color(themedContext.getColor(id))
    val background = color(R.color.mm_background)
    val surface = color(R.color.mm_surface)
    val elevated = color(R.color.mm_surface_elevated)
    val text = color(R.color.mm_text_primary)
    val scheme = (if (darkTheme) darkColorScheme() else lightColorScheme()).copy(
        primary = color(R.color.mm_primary),
        onPrimary = color(R.color.mm_on_accent),
        primaryContainer = elevated,
        onPrimaryContainer = text,
        inversePrimary = color(R.color.mm_inverse_primary),
        secondary = color(R.color.mm_secondary),
        onSecondary = color(R.color.mm_on_accent),
        secondaryContainer = elevated,
        onSecondaryContainer = text,
        tertiary = color(R.color.mm_tertiary),
        onTertiary = color(R.color.mm_on_accent),
        tertiaryContainer = elevated,
        onTertiaryContainer = text,
        background = background,
        onBackground = text,
        surface = surface,
        onSurface = text,
        surfaceVariant = elevated,
        onSurfaceVariant = color(R.color.mm_text_muted),
        surfaceTint = color(R.color.mm_primary),
        inverseSurface = color(R.color.mm_inverse_surface),
        inverseOnSurface = color(R.color.mm_inverse_on_surface),
        surfaceDim = background,
        surfaceBright = elevated,
        surfaceContainerLowest = background,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = elevated,
        surfaceContainerHighest = elevated,
        outline = color(R.color.mm_outline),
        outlineVariant = color(R.color.mm_outline),
        error = color(R.color.mm_error),
        onError = color(R.color.mm_on_error),
        errorContainer = color(R.color.mm_error_container),
        onErrorContainer = color(R.color.mm_on_error_container),
        scrim = color(R.color.mm_scrim)
    )
    return scheme to AdditionalColors(
        textFaint = color(R.color.mm_text_faint),
        success = color(R.color.mm_success),
        onSuccess = color(R.color.mm_on_success),
        disabledContainer = color(R.color.mm_disabled_container),
        onDisabled = color(R.color.mm_on_disabled)
    )
}

@Composable
fun MeetMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val (colors, additionalColors) = remember(context, configuration, darkTheme) {
        loadThemeColors(context, darkTheme)
    }
    CompositionLocalProvider(LocalAdditionalColors provides additionalColors) {
        MaterialTheme(
            colorScheme = colors,
            typography = MeetMindTypography,
            shapes = MeetMindShapes,
            content = content
        )
    }
}
