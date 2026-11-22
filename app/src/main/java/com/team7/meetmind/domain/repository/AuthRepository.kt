package com.team7.meetmind.domain.repository

import com.team7.meetmind.domain.model.SessionState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeSession(): Flow<SessionState>

    suspend fun login(email: String, password: String): Result<Unit>

    /** Tạo tài khoản và đăng nhập luôn. */
    suspend fun register(name: String, email: String, password: String): Result<Unit>

    /** Gửi email chứa liên kết đặt lại mật khẩu (người dùng đặt mật khẩu mới trên trang của Firebase). */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun logout(): Result<Unit>
}
