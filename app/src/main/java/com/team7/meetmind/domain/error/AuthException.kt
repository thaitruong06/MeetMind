package com.team7.meetmind.domain.error

/**
 * Các lỗi nghiệp vụ của luồng xác thực. Tầng domain chỉ biết "chuyện gì đã sai",
 * còn câu chữ hiển thị (tiếng Việt) do tầng presentation quyết định.
 */
sealed class AuthException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    // --- Lỗi kiểm tra dữ liệu nhập (xảy ra ngay trên máy, chưa gọi mạng) ---
    class InvalidEmail : AuthException()
    class EmptyName : AuthException()
    class EmptyPassword : AuthException()
    class PasswordTooShort(val minLength: Int) : AuthException()
    class PasswordMismatch : AuthException()
    class InvalidCodeFormat : AuthException()

    // --- Lỗi do server Supabase trả về ---
    class InvalidCredentials : AuthException()
    class EmailAlreadyRegistered : AuthException()
    class EmailNotConfirmed : AuthException()
    class InvalidOrExpiredCode : AuthException()
    class WeakPassword : AuthException()
    class RateLimited : AuthException()
    class Network(cause: Throwable?) : AuthException(cause = cause)
    class Unknown(val detail: String?, cause: Throwable? = null) : AuthException(detail, cause)
}
