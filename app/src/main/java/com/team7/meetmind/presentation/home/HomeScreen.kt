package com.team7.meetmind.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team7.meetmind.presentation.auth.components.clickableNoRipple
import com.team7.meetmind.presentation.common.EmptyView
import com.team7.meetmind.presentation.common.ErrorView
import com.team7.meetmind.presentation.common.LoadingView
import com.team7.meetmind.presentation.common.MeetMindBottomBar
import com.team7.meetmind.presentation.common.MeetingCard
import com.team7.meetmind.presentation.navigation.Routes
import com.team7.meetmind.presentation.theme.SignalAmber
import com.team7.meetmind.presentation.theme.SignalViolet
import com.team7.meetmind.presentation.theme.TextMuted
import com.team7.meetmind.presentation.theme.TextPrimary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onMeetingClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onRecordClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }

    Scaffold(
        bottomBar = {
            MeetMindBottomBar(currentRoute = Routes.HOME, onNavigate = onNavigate, onRecordClick = onRecordClick)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Xin chào,", color = TextMuted, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            uiState.userName.ifBlank { "bạn" },
                            color = TextPrimary,
                            style = MaterialTheme.typography.displaySmall,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Tìm kiếm", tint = TextMuted)
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(SignalAmber.copy(alpha = 0.18f), SignalViolet.copy(alpha = 0.18f))
                            )
                        )
                        .clickableNoRipple(onRecordClick)
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = null, tint = SignalAmber)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Bắt đầu ghi âm cuộc họp",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "MeetMind sẽ tự phiên âm và tóm tắt cho bạn",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
            }
            item {
                Text("Cuộc họp gần đây", color = TextPrimary, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
            }
            when {
                uiState.isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) { LoadingView() }
                }
                uiState.errorMessage != null && uiState.meetings.isEmpty() -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        ErrorView(message = uiState.errorMessage.orEmpty(), onRetry = viewModel::refresh)
                    }
                }
                uiState.meetings.isEmpty() -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        EmptyView("Chưa có cuộc họp nào. Bấm nút micro để ghi âm cuộc họp đầu tiên.")
                    }
                }
                else -> items(uiState.meetings, key = { it.id }) { meeting ->
                    MeetingCard(meeting = meeting, onClick = { onMeetingClick(meeting.id) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
