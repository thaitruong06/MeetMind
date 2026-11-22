package com.team7.meetmind.data.mapper

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.error.DataException
import java.io.IOException

/** Chuyển lỗi Firestore thành DataException của domain; lỗi không nhận diện được giữ nguyên. */
internal fun Throwable.toDataException(): Throwable {
    if (this is AuthException || this is DataException) return this

    val chain = generateSequence(this) { it.cause }.toList()
    val firestoreCode = chain.filterIsInstance<FirebaseFirestoreException>().firstOrNull()?.code

    return when {
        firestoreCode == Code.UNAVAILABLE || firestoreCode == Code.DEADLINE_EXCEEDED -> DataException.Network(this)
        firestoreCode == Code.PERMISSION_DENIED || firestoreCode == Code.UNAUTHENTICATED -> DataException.PermissionDenied()
        firestoreCode == Code.NOT_FOUND -> DataException.NotFound()
        chain.any { it is IOException } -> DataException.Network(this)
        else -> this
    }
}

internal fun <T> Result<T>.mapDataFailure(): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it.toDataException()) }
    )
