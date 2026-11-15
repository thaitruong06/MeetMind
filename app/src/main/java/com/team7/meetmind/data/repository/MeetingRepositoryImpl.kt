package com.team7.meetmind.data.repository

import com.team7.meetmind.core.suspendRunCatching
import com.team7.meetmind.data.mapper.toDomain
import com.team7.meetmind.data.remote.dto.ActionItemDonePatch
import com.team7.meetmind.data.remote.dto.ActionItemDto
import com.team7.meetmind.data.remote.dto.CalendarEventDto
import com.team7.meetmind.data.remote.dto.FolderDto
import com.team7.meetmind.data.remote.dto.MeetingDto
import com.team7.meetmind.data.remote.dto.TranscriptLineDto
import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.model.MeetingDetail
import com.team7.meetmind.domain.repository.MeetingRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant

/**
 * Đọc/ghi dữ liệu cuộc họp qua Supabase Postgrest. Bảo mật theo từng người dùng được đảm bảo bằng
 * Row Level Security ở phía database (xem supabase/schema.sql), nên ở đây không cần lọc theo user_id.
 */
class MeetingRepositoryImpl(
    private val supabase: SupabaseClient
) : MeetingRepository {

    private val meetingColumns = Columns.raw(
        "id,title,summary,started_at,duration_seconds,tags,participants,folders(name,color),action_items(done)"
    )

    override suspend fun getMeetings(): Result<List<Meeting>> = suspendRunCatching {
        supabase.from("meetings")
            .select(meetingColumns) {
                order("started_at", Order.DESCENDING)
            }
            .decodeList<MeetingDto>()
            .map { it.toDomain() }
    }

    override suspend fun getMeetingDetail(meetingId: String): Result<MeetingDetail> = suspendRunCatching {
        val meeting = supabase.from("meetings")
            .select(meetingColumns) {
                filter { eq("id", meetingId) }
            }
            .decodeSingle<MeetingDto>()

        val transcript = supabase.from("transcript_lines")
            .select {
                filter { eq("meeting_id", meetingId) }
                order("start_seconds", Order.ASCENDING)
            }
            .decodeList<TranscriptLineDto>()

        val actionItems = supabase.from("action_items")
            .select {
                filter { eq("meeting_id", meetingId) }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<ActionItemDto>()

        MeetingDetail(
            meeting = meeting.toDomain(),
            transcript = transcript.map { it.toDomain() },
            actionItems = actionItems.map { it.toDomain() }
        )
    }

    override suspend fun getFolders(): Result<List<Folder>> = suspendRunCatching {
        supabase.from("folders")
            .select(Columns.raw("id,name,color,meetings(count)")) {
                order("created_at", Order.ASCENDING)
            }
            .decodeList<FolderDto>()
            .map { it.toDomain() }
    }

    override suspend fun getUpcomingEvents(): Result<List<CalendarEvent>> = suspendRunCatching {
        supabase.from("calendar_events")
            .select {
                filter { gte("ends_at", Instant.now().toString()) }
                order("starts_at", Order.ASCENDING)
            }
            .decodeList<CalendarEventDto>()
            .map { it.toDomain() }
    }

    override suspend fun setActionItemDone(actionItemId: String, done: Boolean): Result<Unit> =
        suspendRunCatching {
            supabase.from("action_items").update(ActionItemDonePatch(done)) {
                filter { eq("id", actionItemId) }
            }
            Unit
        }
}
