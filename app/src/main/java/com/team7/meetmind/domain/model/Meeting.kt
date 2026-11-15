package com.team7.meetmind.domain.model

import java.time.Instant
import java.time.LocalDate

data class Folder(
    val id: String,
    val name: String,
    val colorHex: String,
    val meetingCount: Int
)

data class Meeting(
    val id: String,
    val title: String,
    val summary: String,
    val startedAt: Instant,
    val durationSeconds: Int,
    val folderName: String?,
    val folderColorHex: String?,
    val tags: List<String>,
    val participants: List<String>,
    val pendingActionItems: Int
)

data class TranscriptLine(
    val id: String,
    val speaker: String,
    val startSeconds: Int,
    val text: String
)

data class ActionItem(
    val id: String,
    val content: String,
    val assignee: String,
    val dueDate: LocalDate?,
    val done: Boolean
)

data class MeetingDetail(
    val meeting: Meeting,
    val transcript: List<TranscriptLine>,
    val actionItems: List<ActionItem>
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val startsAt: Instant,
    val endsAt: Instant,
    val autoRecord: Boolean
)
