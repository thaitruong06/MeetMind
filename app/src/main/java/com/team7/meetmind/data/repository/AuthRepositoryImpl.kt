package com.team7.meetmind.data.repository

import com.team7.meetmind.core.suspendRunCatching
import com.team7.meetmind.data.mapper.mapAuthFailure
import com.team7.meetmind.domain.model.RegisterOutcome
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.model.User
import com.team7.meetmind.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {

    private val auth get() = supabase.auth

    /**
     * Khi đang đặt lại mật khẩu bằng mã OTP, Supabase tạo phiên đăng nhập ngay sau bước xác minh mã,
     * TRƯỚC khi mật khẩu mới được lưu. Cờ này giữ yên trạng thái phiên gửi lên giao diện cho đến khi
     * cả hai bước xong, để app không nhảy sang màn hình chính giữa chừng.
     */
    private val sessionUpdatesPaused = MutableStateFlow(false)

    override fun observeSession(): Flow<SessionState> =
        combine(auth.sessionStatus, sessionUpdatesPaused) { status, paused -> status to paused }
            .filter { (_, paused) -> !paused }
            .map { (status, _) -> status.toDomain() }
            .distinctUntilChanged()

    override suspend fun login(email: String, password: String): Result<Unit> =
        suspendRunCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Unit
        }.mapAuthFailure()

    override suspend fun register(name: String, email: String, password: String): Result<RegisterOutcome> =
        suspendRunCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject { put("full_name", name) }
            }
            // Nếu project tắt "Confirm email" thì đã có phiên ngay; ngược lại phải chờ xác nhận email.
            if (auth.currentSessionOrNull() != null) {
                RegisterOutcome.SIGNED_IN
            } else {
                RegisterOutcome.EMAIL_CONFIRMATION_REQUIRED
            }
        }.mapAuthFailure()

    override suspend fun sendPasswordResetCode(email: String): Result<Unit> =
        suspendRunCatching {
            auth.resetPasswordForEmail(email)
            Unit
        }.mapAuthFailure()

    override suspend fun resetPasswordWithCode(email: String, code: String, newPassword: String): Result<Unit> =
        suspendRunCatching {
            sessionUpdatesPaused.value = true
            try {
                auth.verifyEmailOtp(type = OtpType.Email.RECOVERY, email = email, token = code)
                auth.updateUser { password = newPassword }
                Unit
            } catch (t: Throwable) {
                if (t !is CancellationException) runCatching { auth.signOut() }
                throw t
            } finally {
                sessionUpdatesPaused.value = false
            }
        }.mapAuthFailure()

    override suspend fun logout(): Result<Unit> =
        suspendRunCatching {
            try {
                auth.signOut()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Mất mạng thì vẫn xoá phiên trên máy để người dùng đăng xuất được.
                auth.clearSession()
            }
        }

    private fun SessionStatus.toDomain(): SessionState = when (this) {
        is SessionStatus.Initializing -> SessionState.Loading
        is SessionStatus.Authenticated ->
            (session.user ?: auth.currentUserOrNull())?.toDomainUser()?.let { SessionState.Authenticated(it) }
                ?: SessionState.Unauthenticated
        is SessionStatus.NotAuthenticated -> SessionState.Unauthenticated
        // Làm mới token thất bại (thường do mất mạng): vẫn coi là đã đăng nhập để dùng offline.
        is SessionStatus.RefreshFailure ->
            auth.currentUserOrNull()?.toDomainUser()?.let { SessionState.Authenticated(it) }
                ?: SessionState.Unauthenticated
        else -> SessionState.Unauthenticated
    }

    private fun UserInfo.toDomainUser(): User = User(
        id = id,
        email = email.orEmpty(),
        displayName = (userMetadata?.get("full_name") as? JsonPrimitive)?.contentOrNull
    )
}
