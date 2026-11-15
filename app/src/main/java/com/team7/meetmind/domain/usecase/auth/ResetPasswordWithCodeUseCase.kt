package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.repository.AuthRepository

class ResetPasswordWithCodeUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        code: String,
        newPassword: String,
        confirmPassword: String
    ): Result<Unit> {
        val error: AuthException? = when {
            !AuthValidation.isValidCode(code) -> AuthException.InvalidCodeFormat()
            newPassword.length < AuthValidation.MIN_PASSWORD_LENGTH ->
                AuthException.PasswordTooShort(AuthValidation.MIN_PASSWORD_LENGTH)
            newPassword != confirmPassword -> AuthException.PasswordMismatch()
            else -> null
        }
        if (error != null) return Result.failure(error)
        return authRepository.resetPasswordWithCode(email.trim(), code.trim(), newPassword)
    }
}
