package com.team7.meetmind.domain.usecase.auth

import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveSessionUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<SessionState> = authRepository.observeSession()
}
