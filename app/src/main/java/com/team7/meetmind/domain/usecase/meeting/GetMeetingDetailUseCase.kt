package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.model.MeetingDetail
import com.team7.meetmind.domain.repository.MeetingRepository

class GetMeetingDetailUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(meetingId: String): Result<MeetingDetail> =
        meetingRepository.getMeetingDetail(meetingId)
}
