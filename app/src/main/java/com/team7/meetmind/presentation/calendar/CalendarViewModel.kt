package com.team7.meetmind.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.meeting.GetUpcomingEventsUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import com.team7.meetmind.presentation.model.CalendarEventUi
import com.team7.meetmind.presentation.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarUiState(
    val isLoading: Boolean = true,
    val events: List<CalendarEventUi> = emptyList(),
    val errorMessage: String? = null
)

class CalendarViewModel(
    private val getUpcomingEvents: GetUpcomingEventsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.events.isEmpty(), errorMessage = null) }
            getUpcomingEvents()
                .onSuccess { events ->
                    _uiState.update { it.copy(isLoading = false, events = events.map { e -> e.toUi() }) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }
}
