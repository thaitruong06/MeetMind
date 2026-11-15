package com.team7.meetmind.presentation.model

import androidx.compose.ui.graphics.Color

/**
 * Model dành riêng cho giao diện: dữ liệu đã được định dạng sẵn (nhãn ngày giờ tiếng Việt, màu...).
 * Composable chỉ nhận các model này, không đụng tới model của domain.
 */
data class MeetingUi(
    val id: String,
    val title: String,
    val summary: String,
    val dateLabel: String,
    val durationLabel: String,
    val folder: String,
    val folderColor: Color,
    val tags: List<String>,
    val participants: List<String>,
    val pendingActionItems: Int
)

data class TranscriptLineUi(
    val id: String,
    val speaker: String,
    val timeLabel: String,
    val text: String
)

data class ActionItemUi(
    val id: String,
    val content: String,
    val assignee: String,
    val dueLabel: String,
    val done: Boolean
)

data class MeetingDetailUi(
    val meeting: MeetingUi,
    val transcript: List<TranscriptLineUi>,
    val actionItems: List<ActionItemUi>
)

data class FolderUi(
    val id: String,
    val name: String,
    val meetingCount: Int,
    val color: Color
)

data class CalendarEventUi(
    val id: String,
    val title: String,
    val dateLabel: String,
    val timeLabel: String,
    val autoRecord: Boolean
)
