package com.team7.meetmind.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team7.meetmind.ui.theme.InkBackground
import com.team7.meetmind.ui.theme.InkSurface
import com.team7.meetmind.ui.theme.InkSurfaceElevated
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.TextFaint
import com.team7.meetmind.ui.theme.TextMuted
import com.team7.meetmind.ui.theme.TextPrimary
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MeetingCard(meeting: Meeting, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InkSurface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SampleData.folderColor(meeting.folder))
            )
            Spacer(Modifier.width(8.dp))
            Text(meeting.folder, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            Spacer(Modifier.weight(1f))
            Text(meeting.dateLabel, style = MaterialTheme.typography.bodyMedium, color = TextFaint)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            meeting.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            meeting.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ParticipantStack(meeting.participants)
            Spacer(Modifier.width(10.dp))
            Text(meeting.durationLabel, style = MaterialTheme.typography.bodyMedium, color = TextFaint)
            Spacer(Modifier.weight(1f))
            if (meeting.pendingActionItems > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SignalAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${meeting.pendingActionItems} việc cần làm",
                        color = SignalAmber,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun ParticipantStack(participants: List<String>) {
    Row {
        participants.take(3).forEachIndexed { index, initials ->
            Box(
                modifier = Modifier
                    .offset(x = (-6 * index).dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(InkSurfaceElevated)
                    .border(1.dp, InkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp), color = TextPrimary)
            }
        }
    }
}
