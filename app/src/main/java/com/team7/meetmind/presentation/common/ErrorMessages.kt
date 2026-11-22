package com.team7.meetmind.presentation.common

import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.error.DataException
import java.io.IOException

private const val NETWORK_MESSAGE = "Lỗi kết nối mạng, vui lòng kiểm tra Internet và thử lại"

/** Chuyển lỗi (của domain hoặc lỗi hệ thống) thành câu thông báo tiếng Việt cho người dùng. */
fun Throwable.toUserMessage(): String = when (this) {
    is AuthException.InvalidEmail -> "Email không hợp lệ"
    is AuthException.EmptyName -> "Vui lòng nhập họ tên"
    is AuthException.EmptyPassword -> "Vui lòng nhập mật khẩu"
    is AuthException.PasswordTooShort -> "Mật khẩu cần tối thiểu $minLength ký tự"
    is AuthException.PasswordMismatch -> "Mật khẩu nhập lại không khớp"
    is AuthException.InvalidCredentials -> "Email hoặc mật khẩu không đúng"
    is AuthException.EmailAlreadyRegistered -> "Email này đã được đăng ký"
    is AuthException.AccountDisabled -> "Tài khoản này đã bị vô hiệu hoá"
    is AuthException.NotSignedIn -> "Phiên đăng nhập đã hết, vui lòng đăng nhập lại"
    is AuthException.WeakPassword -> "Mật khẩu quá yếu, vui lòng chọn mật khẩu mạnh hơn"
    is AuthException.RateLimited -> "Bạn thao tác quá nhanh, vui lòng thử lại sau ít phút"
    is AuthException.Network, is DataException.Network -> NETWORK_MESSAGE
    is DataException.PermissionDenied -> "Bạn không có quyền truy cập dữ liệu này"
    is DataException.NotFound -> "Không tìm thấy dữ liệu"
    is AuthException.Unknown -> "Đã xảy ra lỗi: ${detail?.lineSequence()?.firstOrNull() ?: "không rõ nguyên nhân"}"
    else -> {
        val isNetwork = generateSequence(this) { it.cause }.any { it is IOException }
        if (isNetwork) {
            NETWORK_MESSAGE
        } else {
            "Đã xảy ra lỗi: ${message?.lineSequence()?.firstOrNull() ?: "không rõ nguyên nhân"}"
        }
    }
}
