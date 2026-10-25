package com.team7.meetmind

import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import com.team7.meetmind.data.settings.ThemePreferences
import com.team7.meetmind.navigation.MeetMindNavGraph
import com.team7.meetmind.ui.theme.MeetMindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences = remember { ThemePreferences(applicationContext) }
            val themeMode by preferences.themeMode.collectAsState()
            val darkTheme = themeMode.isDark(isSystemInDarkTheme())
            MeetMindTheme(darkTheme = darkTheme) {
                val background = MaterialTheme.colorScheme.background.toArgb()
                val surface = MaterialTheme.colorScheme.surface.toArgb()
                SideEffect {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT
                        ) { darkTheme },
                        navigationBarStyle = SystemBarStyle.auto(surface, surface) { darkTheme }
                    )
                    window.setBackgroundDrawable(background.toDrawable())
                }
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MeetMindNavGraph(
                        themeMode = themeMode,
                        onThemeModeChange = preferences::setThemeMode
                    )
                }
            }
        }
    }
}
