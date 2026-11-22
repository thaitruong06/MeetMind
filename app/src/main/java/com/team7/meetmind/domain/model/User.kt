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
