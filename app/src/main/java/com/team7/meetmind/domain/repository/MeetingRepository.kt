package com.team7.meetmind.domain.repository

import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.model.MeetingDetail

interface MeetingRepository {
    suspend fun getMeetings(): Result<List<Meeting>>
    suspend fun getMeetingDetail(meetingId: String): Result<MeetingDetail>
    suspend fun getFolders(): Result<List<Folder>>
    suspend fun getUpcomingEvents(): Result<List<CalendarEvent>>
    suspend fun setActionItemDone(actionItemId: String, done: Boolean): Result<Unit>
}
