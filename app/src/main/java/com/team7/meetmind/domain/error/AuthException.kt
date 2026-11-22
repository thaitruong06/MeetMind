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

    // --- Lỗi do Firebase Authentication trả về ---
    class InvalidCredentials : AuthException()
    class EmailAlreadyRegistered : AuthException()
    class AccountDisabled : AuthException()
    class WeakPassword : AuthException()
    class RateLimited : AuthException()
    class Network(cause: Throwable?) : AuthException(cause = cause)

    /** Gọi dữ liệu khi chưa đăng nhập. */
    class NotSignedIn : AuthException()
    class Unknown(val detail: String?, cause: Throwable? = null) : AuthException(detail, cause)
}
