package com.team7.meetmind.ui.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.graphics.ColorUtils
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.team7.meetmind.R
import com.team7.meetmind.data.settings.ThemePreferences
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.settings.ThemeModeSelector
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeFeatureTest {
    @get:Rule val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    // Tests never overwrite the user's real appearance preference.
    private val storage = context.getSharedPreferences("theme_feature_test", Context.MODE_PRIVATE)
    private lateinit var preferences: ThemePreferences

    @Before fun setUp() {
        storage.edit().clear().commit()
        preferences = ThemePreferences(storage)
    }

    @After fun tearDown() {
        storage.edit().clear().commit()
    }

    @Test fun selectionUpdatesPaletteAndPreservesScreenState() {
        preferences.setThemeMode(ThemeMode.LIGHT)
        var background = Color.Unspecified
        var folderColor = Color.Unspecified
        compose.setContent {
            val mode by preferences.themeMode.collectAsState()
            MeetMindTheme(darkTheme = mode.isDark(isSystemInDarkTheme())) {
                var count by remember { mutableStateOf(0) }
                val currentBackground = MaterialTheme.colorScheme.background
                val currentFolderColor = SampleData.folders.first().color
                SideEffect {
                    background = currentBackground
                    folderColor = currentFolderColor
                }
                Column {
                    ThemeModeSelector(mode, preferences::setThemeMode)
                    Button(onClick = { count++ }) { Text("Count: $count") }
                }
            }
        }
        compose.onNodeWithText("Count: 0").performClick()
        compose.onNodeWithText(context.getString(R.string.theme_light)).assertIsSelected()
        var lightAccent = Color.Unspecified
        compose.runOnIdle {
            assertTrue(background.luminance() > 0.5f)
            lightAccent = folderColor
        }
        compose.onNodeWithText(context.getString(R.string.theme_dark)).performClick().assertIsSelected()
        compose.onNodeWithText("Count: 1").assertExists()
        compose.runOnIdle {
            assertTrue(background.luminance() < 0.05f)
            assertNotEquals(lightAccent, folderColor)
            assertEquals(ThemeMode.DARK, ThemePreferences(storage).themeMode.value)
        }
        compose.onNodeWithText(context.getString(R.string.theme_light)).performClick().assertIsSelected()
        compose.onNodeWithText("Count: 1").assertExists()
        compose.runOnIdle { assertTrue(background.luminance() > 0.5f) }
    }

    @Test fun systemModeFollowsConfigurationAndExplicitModeOverridesIt() {
        val systemDark = mutableStateOf(false)
        var background = Color.Unspecified
        compose.setContent {
            val mode by preferences.themeMode.collectAsState()
            val configuration = Configuration(LocalConfiguration.current).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                    if (systemDark.value) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
            }
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                MeetMindTheme(darkTheme = mode.isDark(isSystemInDarkTheme())) {
                    val current = MaterialTheme.colorScheme.background
                    SideEffect { background = current }
                    ThemeModeSelector(mode, preferences::setThemeMode)
                }
            }
        }
        compose.onNodeWithText(context.getString(R.string.theme_system)).assertIsSelected()
        compose.runOnIdle { assertTrue(background.luminance() > 0.5f); systemDark.value = true }
        compose.runOnIdle { assertTrue(background.luminance() < 0.05f) }
        compose.onNodeWithText(context.getString(R.string.theme_light)).performClick()
        compose.runOnIdle { assertTrue(background.luminance() > 0.5f) }
        compose.onNodeWithText(context.getString(R.string.theme_dark)).performClick()
        compose.runOnIdle { systemDark.value = false }
        compose.runOnIdle { assertTrue(background.luminance() < 0.05f) }
        compose.onNodeWithText(context.getString(R.string.theme_system)).performClick()
        compose.runOnIdle { assertTrue(background.luminance() > 0.5f) }
    }

    @Test fun storedChoiceIsRestoredAndUnknownValuesFollowSystem() {
        assertEquals(ThemeMode.SYSTEM, preferences.themeMode.value)
        for (mode in listOf(ThemeMode.DARK, ThemeMode.LIGHT, ThemeMode.SYSTEM)) {
            preferences.setThemeMode(mode)
            // Drain pending writes, then read through a fresh preference owner.
            assertTrue(storage.edit().commit())
            assertEquals(mode, ThemePreferences(storage).themeMode.value)
        }
        storage.edit().putString("theme_mode", "unknown-future-mode").commit()
        assertEquals(ThemeMode.SYSTEM, ThemePreferences(storage).themeMode.value)
    }

    @Test fun bothPalettesKeepTextAndButtonContentReadable() {
        for (dark in listOf(false, true)) {
            val (colors, extra) = loadThemeColors(context, dark)
            for (surface in listOf(colors.background, colors.surface, colors.surfaceContainerHigh)) {
                for (text in listOf(colors.onSurface, colors.onSurfaceVariant, extra.textFaint)) {
                    assertContrast(text, surface)
                }
            }
            // Include intermediate gradient stops, not just the endpoints.
            for (step in 0..20) {
                assertContrast(colors.onPrimary, lerp(colors.primary, colors.secondary, step / 20f))
            }
            assertContrast(extra.onSuccess, extra.success)
            assertContrast(extra.onDisabled, extra.disabledContainer)
            assertContrast(colors.onError, colors.error)
            assertContrast(colors.inverseOnSurface, colors.inverseSurface)
        }
    }

    private fun assertContrast(foreground: Color, background: Color) {
        val ratio = ColorUtils.calculateContrast(foreground.toArgb(), background.toArgb())
        assertTrue("Contrast $ratio for $foreground on $background is below 4.5:1", ratio >= 4.5)
    }
}
