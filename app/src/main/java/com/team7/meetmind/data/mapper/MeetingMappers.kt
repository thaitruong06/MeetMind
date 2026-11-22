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
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** [folder] là thư mục mà cuộc họp thuộc về (nếu có); [pendingActionItems] đã được đếm sẵn ở repository. */
internal fun MeetingDto.toDomain(folder: FolderDto?, pendingActionItems: Int): Meeting = Meeting(
    id = id,
    title = title,
    summary = summary,
    startedAt = startedAt.toInstant(),
    durationSeconds = durationSeconds,
    folderName = folder?.name,
    folderColorHex = folder?.color,
    tags = tags,
    participants = participants,
    pendingActionItems = pendingActionItems,
)

internal fun FolderDto.toDomain(meetingCount: Int): Folder = Folder(
    id = id,
    name = name,
    colorHex = color,
    meetingCount = meetingCount,
)

internal fun TranscriptLineDto.toDomain(): TranscriptLine = TranscriptLine(
    id = id,
    speaker = speaker,
    startSeconds = startSeconds,
    text = text,
)

internal fun ActionItemDto.toDomain(): ActionItem = ActionItem(
    id = id,
    content = content,
    assignee = assignee,
    dueDate = dueDate?.let { try { LocalDate.parse(it) } catch (_: DateTimeParseException) { null } },
    done = done,
)

internal fun CalendarEventDto.toDomain(): CalendarEvent = CalendarEvent(
    id = id,
    title = title,
    startsAt = startsAt.toInstant(),
    endsAt = endsAt.toInstant(),
    autoRecord = autoRecord,
)
