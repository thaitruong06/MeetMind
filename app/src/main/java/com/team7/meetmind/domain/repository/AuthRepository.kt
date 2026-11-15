package com.team7.meetmind.domain.repository

import com.team7.meetmind.domain.model.RegisterOutcome
import com.team7.meetmind.domain.model.SessionState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeSession(): Flow<SessionState>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(name: String, email: String, password: String): Result<RegisterOutcome>

    /** Gửi mã xác nhận (OTP) đặt lại mật khẩu tới email. */
    suspend fun sendPasswordResetCode(email: String): Result<Unit>

    /** Kiểm tra mã OTP rồi đặt mật khẩu mới. Thành công thì người dùng được đăng nhập luôn. */
    suspend fun resetPasswordWithCode(email: String, code: String, newPassword: String): Result<Unit>

    suspend fun logout(): Result<Unit>
}
