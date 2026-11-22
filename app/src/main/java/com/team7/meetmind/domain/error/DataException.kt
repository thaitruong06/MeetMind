package com.team7.meetmind.domain.error

/** Lỗi nghiệp vụ khi đọc/ghi dữ liệu cuộc họp (không phụ thuộc Firestore). */
sealed class DataException(cause: Throwable? = null) : Exception(cause) {
    class Network(cause: Throwable? = null) : DataException(cause)
    class PermissionDenied : DataException()
    class NotFound : DataException()
}
