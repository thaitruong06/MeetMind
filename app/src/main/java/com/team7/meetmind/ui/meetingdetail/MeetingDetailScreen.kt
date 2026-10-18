package com.team7.meetmind.ui.meetingdetail

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.components.clickableNoRipple
import com.team7.meetmind.ui.common.ActionItemUi
import com.team7.meetmind.ui.common.ParticipantStack
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.common.TranscriptLine
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.InkSurfaceElevated
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalCoral
import com.team7.meetmind.ui.theme.SignalViolet
import com.team7.meetmind.ui.theme.StateSuccess
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary

@Composable
fun MeetingDetailScreen(meetingId: String, onBack: () -> Unit) {
    val meeting = remember(meetingId) { SampleData.meetingById(meetingId) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tóm tắt", "Transcript", "Việc cần làm")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        meeting.title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(meeting.dateLabel, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = { /* TODO: xuất PDF/Word/TXT, chia sẻ */ }) {
                    Icon(Icons.Filled.IosShare, contentDescription = "Xuất / Chia sẻ", tint = TextMuted)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SegmentedTabs(tabs, selectedTab) { selectedTab = it }
            when (selectedTab) {
                0 -> SummaryTab(meeting.summary, meeting.durationLabel, meeting.folder, meeting.participants)
                1 -> TranscriptTab()
                else -> ActionItemsTab()
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
private fun SummaryTab(summary: String, duration: String, folder: String, participants: List<String>) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ParticipantStack(participants)
            Spacer(Modifier.width(10.dp))
            Text("$duration · $folder", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        Text("Tóm tắt do AI tạo", color = TextFaint, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Text(summary, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
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
private fun TranscriptTab() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(SampleData.transcript) { line -> TranscriptBubble(line) }
    }
}

@Composable
private fun TranscriptBubble(line: TranscriptLine) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(speakerColor(line.speaker)))
            Spacer(Modifier.width(6.dp))
            Text(line.speaker, color = TextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Text(line.time, color = TextFaint, style = MaterialTheme.typography.bodyMedium)
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

private fun speakerColor(name: String): Color = when (name) {
    "Thái" -> SignalAmber
    "Phú" -> SignalViolet
    else -> SignalCoral
}

@Composable
private fun ActionItemsTab() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(SampleData.actionItems, key = { it.id }) { item -> ActionItemRow(item) }
    }
}

@Composable
private fun ActionItemRow(item: ActionItemUi) {
    var done by remember { mutableStateOf(item.done) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkSurface)
            .clickableNoRipple { done = !done }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (done) StateSuccess else Color.Transparent)
                .border(1.5.dp, if (done) StateSuccess else TextFaint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (done) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.content,
                color = if (done) TextFaint else TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (done) TextDecoration.LineThrough else null
            )
            Spacer(Modifier.height(2.dp))
            Text("${item.assignee} · hạn ${item.due}", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
