package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.repository.MeetingRepository

class GetMeetingsUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(): Result<List<Meeting>> = meetingRepository.getMeetings()
}
