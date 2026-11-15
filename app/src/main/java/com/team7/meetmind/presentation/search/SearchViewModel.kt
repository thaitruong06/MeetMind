package com.team7.meetmind.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.meeting.SearchMeetingsUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import com.team7.meetmind.presentation.model.MeetingUi
import com.team7.meetmind.presentation.model.toUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = true,
    val results: List<MeetingUi> = emptyList(),
    val errorMessage: String? = null
)

class SearchViewModel(
    private val searchMeetings: SearchMeetingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        search(delayMillis = 0)
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        search(delayMillis = DEBOUNCE_MILLIS)
    }

    fun retry() = search(delayMillis = 0)

    private fun search(delayMillis: Long) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (delayMillis > 0) delay(delayMillis) // chờ người dùng gõ xong, tránh gọi liên tục
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            searchMeetings(_uiState.value.query)
                .onSuccess { meetings ->
                    _uiState.update { it.copy(isLoading = false, results = meetings.map { m -> m.toUi() }) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 300L
    }
}
