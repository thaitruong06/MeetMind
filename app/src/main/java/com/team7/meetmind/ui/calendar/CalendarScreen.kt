package com.team7.meetmind.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team7.meetmind.navigation.Routes
import com.team7.meetmind.ui.common.CalendarEventUi
import com.team7.meetmind.ui.common.MeetMindBottomBar
import com.team7.meetmind.ui.common.MeetMindTopBar
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary

@Composable
fun CalendarScreen(onNavigate: (String) -> Unit, onRecordClick: () -> Unit) {
    Scaffold(
        topBar = { MeetMindTopBar(title = "Lịch họp") },
        bottomBar = { MeetMindBottomBar(currentRoute = Routes.CALENDAR, onNavigate = onNavigate, onRecordClick = onRecordClick) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Các cuộc họp tick \"Tự ghi\" sẽ được MeetMind tự tạo bản ghi khi đến giờ.",
                    color = TextFaint,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
            }
            items(SampleData.calendarEvents) { event -> CalendarEventCard(event) }
        }
    }
}

@Composable
private fun CalendarEventCard(event: CalendarEventUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(event.dateLabel, color = TextFaint, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(event.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(event.timeLabel, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
        }
        if (event.willAutoRecord) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SignalAmber.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Mic, contentDescription = null, tint = SignalAmber, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tự ghi", color = SignalAmber, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp))
            }
        }
    }
}
