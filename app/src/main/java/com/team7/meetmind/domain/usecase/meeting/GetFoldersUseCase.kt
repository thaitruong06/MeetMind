package com.team7.meetmind.domain.usecase.meeting

import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.repository.MeetingRepository

class GetFoldersUseCase(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(): Result<List<Folder>> = meetingRepository.getFolders()
}
