package com.team7.meetmind.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.model.RegisterOutcome
import com.team7.meetmind.domain.usecase.auth.RegisterUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Khác null khi cần người dùng xác nhận email trước khi đăng nhập. */
    val confirmationEmail: String? = null
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onRegisterClick(name: String, email: String, password: String, confirmPassword: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            registerUseCase(name, email, password, confirmPassword)
                .onSuccess { outcome ->
                    _uiState.update {
                        when (outcome) {
                            // Đã có phiên: navigation tự chuyển vào app.
                            RegisterOutcome.SIGNED_IN -> it.copy(isLoading = false)
                            RegisterOutcome.EMAIL_CONFIRMATION_REQUIRED ->
                                it.copy(isLoading = false, confirmationEmail = email.trim())
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
