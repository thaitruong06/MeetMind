package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        val error: AuthException? = when {
            name.isBlank() -> AuthException.EmptyName()
            !AuthValidation.isValidEmail(email) -> AuthException.InvalidEmail()
            password.length < AuthValidation.MIN_PASSWORD_LENGTH ->
                AuthException.PasswordTooShort(AuthValidation.MIN_PASSWORD_LENGTH)
            password != confirmPassword -> AuthException.PasswordMismatch()
            else -> null
        }
        if (error != null) return Result.failure(error)
        return authRepository.register(name.trim(), email.trim(), password)
    }
}
