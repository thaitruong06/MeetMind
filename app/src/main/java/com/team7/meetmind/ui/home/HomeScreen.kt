package com.team7.meetmind.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.team7.meetmind.navigation.Routes
import com.team7.meetmind.ui.auth.components.clickableNoRipple
import com.team7.meetmind.ui.common.MeetMindBottomBar
import com.team7.meetmind.ui.common.MeetingCard
import com.team7.meetmind.ui.common.SampleData
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalViolet
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    userName: String,
    onMeetingClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onRecordClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = { MeetMindBottomBar(currentRoute = Routes.HOME, onNavigate = onNavigate, onRecordClick = onRecordClick) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Xin chào,", color = TextMuted, style = MaterialTheme.typography.bodyLarge)
                        Text(userName, color = TextPrimary, style = MaterialTheme.typography.displaySmall)
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
                        Text("Bắt đầu ghi âm cuộc họp", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
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
            items(SampleData.meetings) { meeting ->
                MeetingCard(meeting = meeting, onClick = { onMeetingClick(meeting.id) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
