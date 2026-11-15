package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.repository.MeetingRepository

/**
 * Tìm theo tiêu đề, tóm tắt và thẻ tag. Hiện lọc trên danh sách đã tải về (đủ dùng cho đồ án);
 * khi dữ liệu lớn có thể chuyển sang Postgres full-text search mà không phải sửa tầng presentation.
 */
class SearchMeetingsUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(query: String): Result<List<Meeting>> =
        meetingRepository.getMeetings().map { meetings ->
            val q = query.trim()
            if (q.isEmpty()) {
                meetings
            } else {
                meetings.filter { meeting ->
                    meeting.title.contains(q, ignoreCase = true) ||
                        meeting.summary.contains(q, ignoreCase = true) ||
                        meeting.tags.any { tag -> tag.contains(q, ignoreCase = true) }
                }
            }
        }
}
