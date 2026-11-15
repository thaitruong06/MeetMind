package com.team7.meetmind.presentation.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RecordingUiState(
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false
)

/**
 * Hiện mới chỉ mô phỏng đồng hồ. Khi làm Giai đoạn 4: thêm RecordingRepository (AudioRecord,
 * Foreground Service) + use case StartRecording/StopRecording/StreamTranscript vào đây.
 */
class RecordingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecordingUiState())
    val uiState: StateFlow<RecordingUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    fun onTogglePause() {
        val nowPaused = !_uiState.value.isPaused
        _uiState.update { it.copy(isPaused = nowPaused) }
        if (nowPaused) timerJob?.cancel() else startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }
}
