package com.team7.meetmind.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.usecase.auth.ObserveSessionUseCase
import com.team7.meetmind.domain.usecase.meeting.GetMeetingsUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import com.team7.meetmind.presentation.model.MeetingUi
import com.team7.meetmind.presentation.model.toUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val meetings: List<MeetingUi> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    observeSession: ObserveSessionUseCase,
    private val getMeetings: GetMeetingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            observeSession().collect { session ->
                if (session is SessionState.Authenticated) {
                    _uiState.update { it.copy(userName = session.user.label) }
                }
            }
        }
    }

    /** Gọi mỗi khi màn hình hiện lại (vd. quay về từ chi tiết) để số việc cần làm luôn mới nhất. */
    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            // Chỉ hiện vòng xoay lần đầu; những lần sau làm mới êm, không nháy màn hình.
            _uiState.update { it.copy(isLoading = it.meetings.isEmpty(), errorMessage = null) }
            getMeetings()
                .onSuccess { meetings ->
                    _uiState.update { state ->
                        state.copy(isLoading = false, meetings = meetings.map { it.toUi() })
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }
}
