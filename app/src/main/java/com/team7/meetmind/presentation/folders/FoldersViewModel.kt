package com.team7.meetmind.presentation.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team7.meetmind.domain.usecase.meeting.GetFoldersUseCase
import com.team7.meetmind.presentation.common.toUserMessage
import com.team7.meetmind.presentation.model.FolderUi
import com.team7.meetmind.presentation.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoldersUiState(
    val isLoading: Boolean = true,
    val folders: List<FolderUi> = emptyList(),
    val errorMessage: String? = null
)

class FoldersViewModel(
    private val getFolders: GetFoldersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoldersUiState())
    val uiState: StateFlow<FoldersUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.folders.isEmpty(), errorMessage = null) }
            getFolders()
                .onSuccess { folders ->
                    _uiState.update { it.copy(isLoading = false, folders = folders.map { f -> f.toUi() }) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }
}
