package com.team7.meetmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.team7.meetmind.presentation.navigation.MeetMindRoot
import com.team7.meetmind.presentation.theme.MeetMindTheme
import com.team7.meetmind.presentation.theme.ThemeMode
import com.team7.meetmind.presentation.theme.ThemePreferences
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences: ThemePreferences = get()
        val themeMode = MutableStateFlow(ThemeMode.SYSTEM)
        lifecycleScope.launch { preferences.mode.collect { themeMode.value = it } }
        setContent {
            val mode by themeMode.collectAsState()
            MeetMindTheme(mode) {
                MeetMindRoot()
            }
        }
    }
}
