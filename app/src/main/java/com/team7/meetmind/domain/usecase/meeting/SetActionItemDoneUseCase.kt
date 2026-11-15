package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.repository.MeetingRepository

class SetActionItemDoneUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(actionItemId: String, done: Boolean): Result<Unit> =
        meetingRepository.setActionItemDone(actionItemId, done)
}
