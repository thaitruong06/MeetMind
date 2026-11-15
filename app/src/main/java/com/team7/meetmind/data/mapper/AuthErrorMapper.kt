package com.team7.meetmind.data.mapper

import com.team7.meetmind.domain.error.AuthException
import java.io.IOException

/**
 * Chuyển mọi ngoại lệ thô từ supabase-kt/Ktor thành AuthException của domain.
 * Nhận diện theo nội dung thông báo lỗi chuẩn của Supabase Auth (ổn định giữa các phiên bản SDK).
 */
internal fun Throwable.toAuthException(): AuthException {
    if (this is AuthException) return this

    val chain = generateSequence(this) { it.cause }.toList()
    val text = chain.joinToString(" | ") { it.message.orEmpty() }.lowercase()

    return when {
        "invalid login credentials" in text -> AuthException.InvalidCredentials()
        "user already registered" in text || "already been registered" in text -> AuthException.EmailAlreadyRegistered()
        "email not confirmed" in text -> AuthException.EmailNotConfirmed()
        "token has expired or is invalid" in text || "otp_expired" in text -> AuthException.InvalidOrExpiredCode()
        "password should be at least" in text || "weak_password" in text || "weak password" in text ->
            AuthException.WeakPassword()
        "rate limit" in text || "too many requests" in text || "for security purposes" in text ->
            AuthException.RateLimited()
        chain.any { it is IOException } -> AuthException.Network(this)
        else -> AuthException.Unknown(message, this)
    }
}

internal fun <T> Result<T>.mapAuthFailure(): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure<T>(it.toAuthException()) }
    )
