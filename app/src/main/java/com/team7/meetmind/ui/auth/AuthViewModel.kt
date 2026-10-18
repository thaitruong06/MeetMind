package com.team7.meetmind.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.team7.meetmind.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<FirebaseUser?> = repository.authStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.currentUser)

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val error = validateRegister(name, email, password, confirmPassword)
        if (error != null) {
            _uiState.value = AuthUiState.Error(error)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.register(name.trim(), email.trim(), password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(mapFirebaseError(it)) }
        }
    }

    fun login(email: String, password: String) {
        val error = validateLogin(email, password)
        if (error != null) {
            _uiState.value = AuthUiState.Error(error)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(email.trim(), password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(mapFirebaseError(it)) }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun resetPassword(email: String) {
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Vui lòng nhập email hợp lệ")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.sendPasswordReset(email.trim())
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(mapFirebaseError(it)) }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validateRegister(name: String, email: String, password: String, confirm: String): String? = when {
        name.isBlank() -> "Vui lòng nhập họ tên"
        !isValidEmail(email) -> "Email không hợp lệ"
        password.length < 6 -> "Mật khẩu cần tối thiểu 6 ký tự"
        password != confirm -> "Mật khẩu nhập lại không khớp"
        else -> null
    }

    private fun validateLogin(email: String, password: String): String? = when {
        !isValidEmail(email) -> "Email không hợp lệ"
        password.isBlank() -> "Vui lòng nhập mật khẩu"
        else -> null
    }

    private fun isValidEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun mapFirebaseError(t: Throwable): String {
        val msg = t.message ?: return "Đã xảy ra lỗi, vui lòng thử lại"
        return when {
            msg.contains("email address is already in use", true) -> "Email này đã được đăng ký"
            msg.contains("password is invalid", true) -> "Mật khẩu không đúng"
            msg.contains("no user record", true) -> "Tài khoản không tồn tại"
            msg.contains("badly formatted", true) -> "Email không hợp lệ"
            msg.contains("network error", true) -> "Lỗi kết nối mạng, vui lòng thử lại"
            msg.contains("too many requests", true) -> "Bạn thử quá nhiều lần, vui lòng thử lại sau"
            else -> msg
        }
    }
}
