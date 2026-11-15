package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (!AuthValidation.isValidEmail(email)) return Result.failure(AuthException.InvalidEmail())
        if (password.isEmpty()) return Result.failure(AuthException.EmptyPassword())
        return authRepository.login(email.trim(), password)
    }
}
