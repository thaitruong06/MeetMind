package com.team7.meetmind.presentation.model

import androidx.compose.ui.graphics.Color
import com.team7.meetmind.domain.model.ActionItem
import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.model.MeetingDetail
import com.team7.meetmind.domain.model.TranscriptLine
import com.team7.meetmind.presentation.theme.TextMuted
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val viLocale: Locale = Locale.forLanguageTag("vi-VN")
private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val shortDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM, HH:mm")
private val weekdayDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM", viLocale)
private val fullDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun zone(): ZoneId = ZoneId.systemDefault()

fun parseColor(hex: String?): Color =
    try {
        if (hex.isNullOrBlank()) TextMuted else Color(android.graphics.Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        TextMuted
    }

private fun Instant.toMeetingDateLabel(): String {
    val dateTime = atZone(zone())
    val today = LocalDate.now(zone())
    val time = dateTime.format(timeFormat)
    return when (dateTime.toLocalDate()) {
        today -> "Hôm nay, $time"
        today.minusDays(1) -> "Hôm qua, $time"
        else -> dateTime.format(shortDateTimeFormat)
    }
}

private fun Int.toDurationLabel(): String =
    if (this < 60) "$this giây" else "${this / 60} phút"

private fun Int.toClockLabel(): String = "%02d:%02d".format(this / 60, this % 60)

fun Meeting.toUi(): MeetingUi = MeetingUi(
    id = id,
    title = title,
    summary = summary,
    dateLabel = startedAt.toMeetingDateLabel(),
    durationLabel = durationSeconds.toDurationLabel(),
    folder = folderName ?: "Chưa phân loại",
    folderColor = parseColor(folderColorHex),
    tags = tags,
    participants = participants,
    pendingActionItems = pendingActionItems
)

fun TranscriptLine.toUi(): TranscriptLineUi = TranscriptLineUi(
    id = id,
    speaker = speaker,
    timeLabel = startSeconds.toClockLabel(),
    text = text
)

fun ActionItem.toUi(): ActionItemUi = ActionItemUi(
    id = id,
    content = content,
    assignee = assignee.ifBlank { "Chưa giao" },
    dueLabel = dueDate?.format(fullDateFormat) ?: "không có hạn",
    done = done
)

fun MeetingDetail.toUi(): MeetingDetailUi = MeetingDetailUi(
    meeting = meeting.toUi(),
    transcript = transcript.map { it.toUi() },
    actionItems = actionItems.map { it.toUi() }
)

fun Folder.toUi(): FolderUi = FolderUi(
    id = id,
    name = name,
    meetingCount = meetingCount,
    color = parseColor(colorHex)
)

fun CalendarEvent.toUi(): CalendarEventUi {
    val start = startsAt.atZone(zone())
    val end = endsAt.atZone(zone())
    return CalendarEventUi(
        id = id,
        title = title,
        dateLabel = start.format(weekdayDateFormat).replaceFirstChar { it.uppercase() },
        timeLabel = "${start.format(timeFormat)} - ${end.format(timeFormat)}",
        autoRecord = autoRecord
    )
}
