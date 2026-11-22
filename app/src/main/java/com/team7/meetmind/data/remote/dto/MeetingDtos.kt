package com.team7.meetmind.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * DTO = hình dạng dữ liệu đúng như document trong Firestore (camelCase, ngày giờ dạng Timestamp).
 * Chúng chỉ tồn tại trong tầng data; tầng domain không biết chúng.
 *
 * Đọc thủ công từ DocumentSnapshot (không dùng toObject) để thiếu field thì dùng giá trị mặc định
 * thay vì crash, và không cần constructor rỗng / reflection / luật Proguard.
 */

data class FolderDto(
    val id: String,
    val name: String,
    val color: String,
)

data class MeetingDto(
    val id: String,
    val title: String,
    val summary: String,
    val startedAt: Timestamp,
    val durationSeconds: Int,
    val tags: List<String>,
    val participants: List<String>,
    val folderId: String?,
)

data class TranscriptLineDto(
    val id: String,
    val speaker: String,
    val startSeconds: Int,
    val text: String,
)

data class ActionItemDto(
    val id: String,
    val meetingId: String,
    val content: String,
    val assignee: String,
    /** Dạng "yyyy-MM-dd" (không kèm múi giờ), hoặc null nếu chưa có deadline. */
    val dueDate: String?,
    val done: Boolean,
    val createdAt: Timestamp?,
)

data class CalendarEventDto(
    val id: String,
    val title: String,
    val startsAt: Timestamp,
    val endsAt: Timestamp,
    val autoRecord: Boolean,
)

private const val DEFAULT_FOLDER_COLOR = "#8D8A99"
private val EPOCH = Timestamp(0, 0)

private fun DocumentSnapshot.strings(field: String): List<String> =
    (get(field) as? List<*>)?.filterIsInstance<String>().orEmpty()

internal fun DocumentSnapshot.toFolderDto() = FolderDto(
    id = id,
    name = getString("name").orEmpty(),
    color = getString("color") ?: DEFAULT_FOLDER_COLOR,
)

internal fun DocumentSnapshot.toMeetingDto() = MeetingDto(
    id = id,
    title = getString("title").orEmpty(),
    summary = getString("summary").orEmpty(),
    startedAt = getTimestamp("startedAt") ?: EPOCH,
    durationSeconds = getLong("durationSeconds")?.toInt() ?: 0,
    tags = strings("tags"),
    participants = strings("participants"),
    folderId = getString("folderId"),
)

internal fun DocumentSnapshot.toTranscriptLineDto() = TranscriptLineDto(
    id = id,
    speaker = getString("speaker").orEmpty(),
    startSeconds = getLong("startSeconds")?.toInt() ?: 0,
    text = getString("text").orEmpty(),
)

internal fun DocumentSnapshot.toActionItemDto() = ActionItemDto(
    id = id,
    meetingId = getString("meetingId").orEmpty(),
    content = getString("content").orEmpty(),
    assignee = getString("assignee").orEmpty(),
    dueDate = getString("dueDate"),
    done = getBoolean("done") ?: false,
    createdAt = getTimestamp("createdAt"),
)

internal fun DocumentSnapshot.toCalendarEventDto() = CalendarEventDto(
    id = id,
    title = getString("title").orEmpty(),
    startsAt = getTimestamp("startsAt") ?: EPOCH,
    endsAt = getTimestamp("endsAt") ?: EPOCH,
    autoRecord = getBoolean("autoRecord") ?: false,
)
