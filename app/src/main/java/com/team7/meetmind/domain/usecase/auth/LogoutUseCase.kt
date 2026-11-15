package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = authRepository.logout()
}
