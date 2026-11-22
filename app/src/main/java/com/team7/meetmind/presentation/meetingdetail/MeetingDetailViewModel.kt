package com.team7.meetmind.presentation.meetingdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.meeting.GetMeetingDetailUseCase
import com.team7.meetmind.domain.usecase.meeting.SetActionItemDoneUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import com.team7.meetmind.presentation.model.MeetingDetailUi
import com.team7.meetmind.presentation.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeetingDetailUiState(
    val isLoading: Boolean = true,
    val detail: MeetingDetailUi? = null,
    val errorMessage: String? = null,
    /** Lỗi nhẹ (vd. đổi trạng thái việc thất bại) hiện bằng snackbar, không thay cả màn hình. */
    val transientMessage: String? = null
)

class MeetingDetailViewModel(
    private val meetingId: String,
    private val getMeetingDetail: GetMeetingDetailUseCase,
    private val setActionItemDone: SetActionItemDoneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeetingDetailUiState())
    val uiState: StateFlow<MeetingDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getMeetingDetail(meetingId)
                .onSuccess { detail ->
                    _uiState.update { it.copy(isLoading = false, detail = detail.toUi()) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    /** Cập nhật giao diện ngay (optimistic), rồi lưu lên Firestore; lỗi thì hoàn tác. */
    fun onToggleActionItem(actionItemId: String) {
        val current = _uiState.value.detail ?: return
        val item = current.actionItems.firstOrNull { it.id == actionItemId } ?: return
        val newDone = !item.done
        setLocalDone(actionItemId, newDone)

        viewModelScope.launch {
            setActionItemDone(actionItemId, newDone).onFailure { error ->
                setLocalDone(actionItemId, !newDone)
                _uiState.update { it.copy(transientMessage = error.toUserMessage()) }
            }
        }
    }

    fun onTransientMessageShown() {
        _uiState.update { it.copy(transientMessage = null) }
    }

    private fun setLocalDone(actionItemId: String, done: Boolean) {
        _uiState.update { state ->
            val detail = state.detail ?: return@update state
            state.copy(
                detail = detail.copy(
                    actionItems = detail.actionItems.map { if (it.id == actionItemId) it.copy(done = done) else it }
                )
            )
        }
    }
}
