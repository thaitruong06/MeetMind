package com.team7.meetmind.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team7.meetmind.presentation.auth.components.MeetMindTextField
import com.team7.meetmind.presentation.common.EmptyView
import com.team7.meetmind.presentation.common.ErrorView
import com.team7.meetmind.presentation.common.LoadingView
import com.team7.meetmind.presentation.common.MeetingCard
import com.team7.meetmind.presentation.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onMeetingClick: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
                }
                Spacer(Modifier.width(4.dp))
                MeetMindTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChange,
                    label = "Tìm cuộc họp, từ khóa, thẻ tag...",
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.results.isEmpty() -> LoadingView()
                    uiState.errorMessage != null ->
                        ErrorView(message = uiState.errorMessage.orEmpty(), onRetry = viewModel::retry)
                    uiState.results.isEmpty() -> EmptyView("Không tìm thấy kết quả phù hợp.")
                    else -> LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.results, key = { it.id }) { meeting ->
                            MeetingCard(meeting) { onMeetingClick(meeting.id) }
                        }
                    }
                }
            }
        }
    }
}
