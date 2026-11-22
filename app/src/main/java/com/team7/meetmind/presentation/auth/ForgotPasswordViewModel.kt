package com.team7.meetmind.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.auth.SendPasswordResetEmailUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Khác null sau khi đã gửi email đặt lại mật khẩu thành công. */
    val sentToEmail: String? = null
)

/**
 * Quên mật khẩu: nhập email -> Firebase gửi email chứa liên kết đặt lại mật khẩu.
 * Người dùng đặt mật khẩu mới trên trang của Firebase rồi quay lại app đăng nhập.
 */
class ForgotPasswordViewModel(
    private val sendResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onSendEmail(email: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            sendResetEmailUseCase(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, sentToEmail = email.trim()) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun onResend() {
        _uiState.value.sentToEmail?.let(::onSendEmail)
    }

    fun onChangeEmail() {
        _uiState.update { it.copy(sentToEmail = null, errorMessage = null) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
