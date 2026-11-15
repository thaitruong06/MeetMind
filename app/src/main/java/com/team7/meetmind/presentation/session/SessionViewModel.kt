package com.team7.meetmind.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.usecase.auth.ObserveSessionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Nguồn sự thật duy nhất về việc người dùng đã đăng nhập hay chưa; navigation phản ứng theo nó. */
class SessionViewModel(
    observeSession: ObserveSessionUseCase
) : ViewModel() {
    val sessionState: StateFlow<SessionState> = observeSession()
        .stateIn(viewModelScope, SharingStarted.Eagerly, SessionState.Loading)
}
