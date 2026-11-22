package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.repository.AuthRepository

class SendPasswordResetEmailUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (!AuthValidation.isValidEmail(email)) return Result.failure(AuthException.InvalidEmail())
        return authRepository.sendPasswordResetEmail(email.trim())
    }
}
