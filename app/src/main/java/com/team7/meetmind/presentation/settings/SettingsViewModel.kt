package com.team7.meetmind.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.usecase.auth.LogoutUseCase
import com.team7.meetmind.domain.usecase.auth.ObserveSessionUseCase
import com.team7.meetmind.presentation.theme.ThemeMode
import com.team7.meetmind.presentation.theme.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userLabel: String = "Tài khoản",
    val cloudSyncEnabled: Boolean = true,
    val isLoggingOut: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class SettingsViewModel(
    observeSession: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { themePreferences.mode.collect { mode -> _uiState.update { it.copy(themeMode = mode) } } }
        viewModelScope.launch {
            observeSession().collect { session ->
                if (session is SessionState.Authenticated) {
                    _uiState.update { it.copy(userLabel = session.user.label) }
                }
            }
        }
    }

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { themePreferences.setMode(mode) }
    }

    /** TODO: lưu lựa chọn này vào DataStore khi làm tính năng đồng bộ Cloud. */
    fun onCloudSyncChange(enabled: Boolean) {
        _uiState.update { it.copy(cloudSyncEnabled = enabled) }
    }

    fun onLogoutClick() {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            // Đăng xuất xong, phiên đổi sang "chưa đăng nhập" và navigation tự đưa về màn hình đăng nhập.
            logoutUseCase()
            _uiState.update { it.copy(isLoggingOut = false) }
        }
    }
}
