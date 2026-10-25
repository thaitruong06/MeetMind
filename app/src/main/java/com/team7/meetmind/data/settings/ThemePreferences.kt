package com.team7.meetmind.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.team7.meetmind.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Device-wide preference, independent of the signed-in account. */
class ThemePreferences internal constructor(private val preferences: SharedPreferences) {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("appearance", Context.MODE_PRIVATE)
    )
    private val currentMode = MutableStateFlow(
        ThemeMode.fromPreference(preferences.getString("theme_mode", null))
    )
    val themeMode = currentMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        preferences.edit { putString("theme_mode", mode.preferenceValue) }
        currentMode.value = mode
    }
}
