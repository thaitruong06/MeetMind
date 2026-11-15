package com.team7.meetmind.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO = hình dạng dữ liệu đúng như bảng/JSON của Supabase (snake_case, ngày giờ dạng chuỗi).
 * Chúng chỉ tồn tại trong tầng data; tầng domain không biết chúng.
 */

@Serializable
data class FolderRefDto(
    val name: String,
    val color: String = "#8D8A99"
)

@Serializable
data class DoneFlagDto(val done: Boolean)

@Serializable
data class MeetingDto(
    val id: String,
    val title: String,
    val summary: String = "",
    @SerialName("started_at") val startedAt: String,
    @SerialName("duration_seconds") val durationSeconds: Int = 0,
    val tags: List<String> = emptyList(),
    val participants: List<String> = emptyList(),
    /** Dữ liệu nhúng từ bảng folders (quan hệ nhiều-một), null nếu cuộc họp chưa thuộc thư mục nào. */
    val folders: FolderRefDto? = null,
    /** Dữ liệu nhúng từ bảng action_items, chỉ lấy cột done để đếm việc chưa xong. */
    @SerialName("action_items") val actionItems: List<DoneFlagDto> = emptyList()
)

@Serializable
data class CountDto(val count: Int = 0)

@Serializable
data class FolderDto(
    val id: String,
    val name: String,
    val color: String = "#8D8A99",
    /** PostgREST trả về [{"count": n}] khi nhúng meetings(count). */
    val meetings: List<CountDto> = emptyList()
)

@Serializable
data class TranscriptLineDto(
    val id: String,
    val speaker: String,
    @SerialName("start_seconds") val startSeconds: Int = 0,
    val text: String
)

@Serializable
data class ActionItemDto(
    val id: String,
    val content: String,
    val assignee: String = "",
    @SerialName("due_date") val dueDate: String? = null,
    val done: Boolean = false
)

@Serializable
data class CalendarEventDto(
    val id: String,
    val title: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
    @SerialName("auto_record") val autoRecord: Boolean = false
)

/** Body của lệnh UPDATE chỉ đổi cột done. */
@Serializable
data class ActionItemDonePatch(val done: Boolean)
