package com.team7.meetmind.data.mapper

import com.team7.meetmind.data.remote.dto.ActionItemDto
import com.team7.meetmind.data.remote.dto.CalendarEventDto
import com.team7.meetmind.data.remote.dto.FolderDto
import com.team7.meetmind.data.remote.dto.MeetingDto
import com.team7.meetmind.data.remote.dto.TranscriptLineDto
import com.team7.meetmind.domain.model.ActionItem
import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.model.TranscriptLine
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime

private fun parseInstant(value: String): Instant = OffsetDateTime.parse(value).toInstant()

internal fun MeetingDto.toDomain(): Meeting = Meeting(
    id = id,
    title = title,
    summary = summary,
    startedAt = parseInstant(startedAt),
    durationSeconds = durationSeconds,
    folderName = folders?.name,
    folderColorHex = folders?.color,
    tags = tags,
    participants = participants,
    pendingActionItems = actionItems.count { !it.done }
)

internal fun FolderDto.toDomain(): Folder = Folder(
    id = id,
    name = name,
    colorHex = color,
    meetingCount = meetings.firstOrNull()?.count ?: 0
)

internal fun TranscriptLineDto.toDomain(): TranscriptLine = TranscriptLine(
    id = id,
    speaker = speaker,
    startSeconds = startSeconds,
    text = text
)

internal fun ActionItemDto.toDomain(): ActionItem = ActionItem(
    id = id,
    content = content,
    assignee = assignee,
    dueDate = dueDate?.let { LocalDate.parse(it) },
    done = done
)

internal fun CalendarEventDto.toDomain(): CalendarEvent = CalendarEvent(
    id = id,
    title = title,
    startsAt = parseInstant(startsAt),
    endsAt = parseInstant(endsAt),
    autoRecord = autoRecord
)
