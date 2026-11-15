package com.team7.meetmind.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?
) {
    /** Tên hiển thị; nếu chưa đặt tên thì dùng email. */
    val label: String
        get() = displayName?.takeIf { it.isNotBlank() } ?: email
}

sealed interface SessionState {
    /** Đang đọc phiên đăng nhập đã lưu trên máy. */
    data object Loading : SessionState
    data class Authenticated(val user: User) : SessionState
    data object Unauthenticated : SessionState
}

enum class RegisterOutcome {
    /** Đăng ký xong và đã đăng nhập luôn. */
    SIGNED_IN,

    /** Project Supabase đang bật "Confirm email": người dùng phải bấm link xác nhận trong email. */
    EMAIL_CONFIRMATION_REQUIRED
}
