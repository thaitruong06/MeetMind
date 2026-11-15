package com.team7.meetmind.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.auth.ResetPasswordWithCodeUseCase
import com.team7.meetmind.domain.usecase.auth.SendPasswordResetCodeUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ResetStep { ENTER_EMAIL, ENTER_CODE }

data class ForgotPasswordUiState(
    val step: ResetStep = ResetStep.ENTER_EMAIL,
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

/**
 * Quên mật khẩu theo 2 bước, hoàn toàn trong app (không cần deep link):
 * 1) nhập email -> Supabase gửi mã OTP; 2) nhập mã + mật khẩu mới -> đặt lại và tự đăng nhập.
 */
class ForgotPasswordViewModel(
    private val sendCodeUseCase: SendPasswordResetCodeUseCase,
    private val resetPasswordUseCase: ResetPasswordWithCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onSendCode(email: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            sendCodeUseCase(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = ResetStep.ENTER_CODE,
                            email = email.trim(),
                            infoMessage = "Đã gửi mã xác nhận tới ${email.trim()}. Vui lòng kiểm tra hộp thư (kể cả mục spam)."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun onResendCode() = onSendCode(_uiState.value.email)

    fun onSubmitNewPassword(code: String, newPassword: String, confirmPassword: String) {
        if (_uiState.value.isLoading) return
        val email = _uiState.value.email
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            resetPasswordUseCase(email, code, newPassword, confirmPassword)
                .onSuccess {
                    // Thành công: người dùng đã được đăng nhập, navigation tự chuyển vào app.
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun onChangeEmail() {
        _uiState.update { it.copy(step = ResetStep.ENTER_EMAIL, errorMessage = null, infoMessage = null) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
