package com.team7.meetmind.core

import kotlin.coroutines.cancellation.CancellationException

/**
 * Giống runCatching nhưng KHÔNG nuốt CancellationException, để coroutine bị huỷ
 * (ví dụ khi ViewModel bị clear) vẫn dừng đúng cách.
 */
inline fun <T> suspendRunCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
