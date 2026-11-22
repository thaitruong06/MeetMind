package com.team7.meetmind.data.mapper

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.team7.meetmind.domain.error.AuthException
import java.io.IOException

/** Chuyển mọi ngoại lệ thô từ Firebase Authentication thành AuthException của domain. */
internal fun Throwable.toAuthException(): AuthException {
    if (this is AuthException) return this

    val chain = generateSequence(this) { it.cause }.toList()
    val firebaseAuth = chain.filterIsInstance<FirebaseAuthException>().firstOrNull()

    return when {
        // FirebaseAuthWeakPasswordException là con của FirebaseAuthInvalidCredentialsException => kiểm tra trước.
        chain.any { it is FirebaseAuthWeakPasswordException } -> AuthException.WeakPassword()
        chain.any { it is FirebaseAuthUserCollisionException } -> AuthException.EmailAlreadyRegistered()
        chain.any { it is FirebaseTooManyRequestsException } -> AuthException.RateLimited()
        chain.any { (it is FirebaseNetworkException) || (it is IOException) } -> AuthException.Network(this)
        firebaseAuth?.errorCode == "ERROR_USER_DISABLED" -> AuthException.AccountDisabled()
        firebaseAuth?.errorCode == "ERROR_INVALID_EMAIL" -> AuthException.InvalidEmail()
        // Sai mật khẩu, email chưa đăng ký, thông tin không hợp lệ: Firebase gộp chung để chống dò email.
        chain.any { (it is FirebaseAuthInvalidCredentialsException) || (it is FirebaseAuthInvalidUserException) } ->
            AuthException.InvalidCredentials()
        else -> AuthException.Unknown(firebaseAuth?.let { "${it.errorCode}: ${it.message}" } ?: message, this)
    }
}

internal fun <T> Result<T>.mapAuthFailure(): Result<T> = fold(
    onSuccess = { Result.success(it) },
) { Result.failure(it.toAuthException()) }
