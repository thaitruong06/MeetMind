package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.repository.MeetingRepository

class GetUpcomingEventsUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(): Result<List<CalendarEvent>> = meetingRepository.getUpcomingEvents()
}
