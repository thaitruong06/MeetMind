package com.team7.meetmind.ui.search

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import com.team7.meetmind.ui.auth.components.MeetMindTextField
import com.team7.meetmind.ui.common.MeetingCard
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted

@Composable
fun SearchScreen(onBack: () -> Unit, onMeetingClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) {
            SampleData.meetings
        } else {
            SampleData.meetings.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.summary.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextMuted)
                }
                Spacer(Modifier.width(4.dp))
                MeetMindTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = "Tìm cuộc họp, từ khóa, thẻ tag...",
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            if (results.isEmpty()) {
                Text(
                    "Không tìm thấy kết quả phù hợp.",
                    color = TextFaint,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results) { meeting -> MeetingCard(meeting) { onMeetingClick(meeting.id) } }
                }
            }
        }
    }
}
