package com.team7.meetmind.presentation.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_preferences")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class ThemePreferences(context: Context) {
    private val store = context.applicationContext.themeDataStore
    val mode: Flow<ThemeMode> = store.data.map { values ->
        runCatching { ThemeMode.valueOf(values[MODE_KEY] ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    suspend fun setMode(mode: ThemeMode) {
        store.edit { it[MODE_KEY] = mode.name }
    }

    private companion object { val MODE_KEY = stringPreferencesKey("theme_mode") }
}
