package com.team7.meetmind.presentation.meetingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.team7.meetmind.presentation.auth.components.clickableNoRipple
import com.team7.meetmind.presentation.common.ErrorView
import com.team7.meetmind.presentation.common.LoadingView
import com.team7.meetmind.presentation.common.ParticipantStack
import com.team7.meetmind.presentation.model.ActionItemUi
import com.team7.meetmind.presentation.model.MeetingDetailUi
import com.team7.meetmind.presentation.model.TranscriptLineUi
import com.team7.meetmind.presentation.theme.InkSurface
import com.team7.meetmind.presentation.theme.InkSurfaceElevated
import com.team7.meetmind.presentation.theme.SignalAmber
import com.team7.meetmind.presentation.theme.SignalCoral
import com.team7.meetmind.presentation.theme.SignalViolet
import com.team7.meetmind.presentation.theme.StateSuccess
import com.team7.meetmind.presentation.theme.TextFaint
import com.team7.meetmind.presentation.theme.TextMuted
import com.team7.meetmind.presentation.theme.TextPrimary
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MeetingDetailScreen(
    meetingId: String,
    onBack: () -> Unit,
    viewModel: MeetingDetailViewModel = koinViewModel { parametersOf(meetingId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Tóm tắt", "Transcript", "Việc cần làm")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.transientMessage) {
        uiState.transientMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onTransientMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        uiState.detail?.meeting?.title ?: "Chi tiết cuộc họp",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        uiState.detail?.meeting?.dateLabel.orEmpty(),
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = { /* TODO: xuất PDF/Word/TXT, chia sẻ */ }) {
                    Icon(Icons.Filled.IosShare, contentDescription = "Xuất / Chia sẻ", tint = TextMuted)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val detail = uiState.detail
            when {
                uiState.isLoading -> LoadingView()
                detail == null -> ErrorView(
                    message = uiState.errorMessage ?: "Không tải được cuộc họp",
                    onRetry = viewModel::load
                )
                else -> {
                    SegmentedTabs(tabs, selectedTab) { selectedTab = it }
                    when (selectedTab) {
                        0 -> SummaryTab(detail)
                        1 -> TranscriptTab(detail.transcript)
                        else -> ActionItemsTab(detail.actionItems, viewModel::onToggleActionItem)
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabs(tabs: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InkSurface)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) InkSurfaceElevated else Color.Transparent)
                    .clickableNoRipple { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = if (isSelected) SignalAmber else TextMuted, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun SummaryTab(detail: MeetingDetailUi) {
    val meeting = detail.meeting
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ParticipantStack(meeting.participants)
            Spacer(Modifier.width(10.dp))
            Text("${meeting.durationLabel} · ${meeting.folder}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Text("Tóm tắt do AI tạo", color = TextFaint, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            meeting.summary.ifBlank { "Chưa có tóm tắt cho cuộc họp này." },
            color = TextPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))
        Text("Xuất biên bản", color = TextFaint, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExportChip("PDF")
            ExportChip("Word")
            ExportChip("TXT")
        }
    }
}

@Composable
private fun ExportChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(InkSurface)
            .clickableNoRipple { /* TODO: xuất file */ }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(label, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TranscriptTab(lines: List<TranscriptLineUi>) {
    if (lines.isEmpty()) {
        Text(
            "Chưa có transcript cho cuộc họp này.",
            color = TextFaint,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(20.dp)
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(lines, key = { it.id }) { line -> TranscriptBubble(line) }
    }
}

@Composable
private fun TranscriptBubble(line: TranscriptLineUi) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(speakerColor(line.speaker)))
            Spacer(Modifier.width(6.dp))
            Text(line.speaker, color = TextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Text(line.timeLabel, color = TextFaint, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            line.text,
            color = TextMuted,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

/** Mỗi người nói một màu, lấy cố định theo tên để cùng một người luôn cùng màu. */
private fun speakerColor(name: String): Color {
    val palette = listOf(SignalAmber, SignalViolet, SignalCoral, StateSuccess)
    return palette[Math.floorMod(name.hashCode(), palette.size)]
}

@Composable
private fun ActionItemsTab(items: List<ActionItemUi>, onToggle: (String) -> Unit) {
    if (items.isEmpty()) {
        Text(
            "Cuộc họp này chưa có việc cần làm.",
            color = TextFaint,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(20.dp)
        )
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item -> ActionItemRow(item) { onToggle(item.id) } }
    }
}

@Composable
private fun ActionItemRow(item: ActionItemUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkSurface)
            .clickableNoRipple(onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (item.done) StateSuccess else Color.Transparent)
                .border(1.5.dp, if (item.done) StateSuccess else TextFaint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (item.done) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.content,
                color = if (item.done) TextFaint else TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.done) TextDecoration.LineThrough else null
            )
            Spacer(Modifier.height(2.dp))
            Text("${item.assignee} · hạn ${item.dueLabel}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
