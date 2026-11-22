package com.team7.meetmind.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.team7.meetmind.core.suspendRunCatching
import com.team7.meetmind.data.mapper.mapDataFailure
import com.team7.meetmind.data.mapper.toDomain
import com.team7.meetmind.data.remote.dto.toActionItemDto
import com.team7.meetmind.data.remote.dto.toCalendarEventDto
import com.team7.meetmind.data.remote.dto.toFolderDto
import com.team7.meetmind.data.remote.dto.toMeetingDto
import com.team7.meetmind.data.remote.dto.toTranscriptLineDto
import com.team7.meetmind.domain.error.AuthException
import com.team7.meetmind.domain.error.DataException
import com.team7.meetmind.domain.model.CalendarEvent
import com.team7.meetmind.domain.model.Folder
import com.team7.meetmind.domain.model.Meeting
import com.team7.meetmind.domain.model.MeetingDetail
import com.team7.meetmind.domain.repository.MeetingRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

/**
 * Đọc/ghi dữ liệu cuộc họp qua Cloud Firestore. Mọi dữ liệu nằm dưới users/{uid}/... và
 * Firestore Security Rules (firebase/firestore.rules) chỉ cho đúng chủ tài khoản truy cập.
 *
 * Cấu trúc:
 *   users/{uid}/folders/{folderId}
 *   users/{uid}/meetings/{meetingId}
 *   users/{uid}/meetings/{meetingId}/transcriptLines/{lineId}
 *   users/{uid}/actionItems/{itemId}          (có field meetingId)
 *   users/{uid}/calendarEvents/{eventId}
 *
 * Firestore không có JOIN nên số việc chưa xong / số cuộc họp trong thư mục được đếm ở đây,
 * không lưu dư thừa trong document (tránh lệch số liệu).
 */
class MeetingRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : MeetingRepository {

    private fun userDoc(): DocumentReference {
        val uid = auth.currentUser?.uid ?: throw AuthException.NotSignedIn()
        return firestore.collection(USERS).document(uid)
    }

    override suspend fun getMeetings(): Result<List<Meeting>> = suspendRunCatching {
        val user = userDoc()
        coroutineScope {
            val folders = async { user.collection(FOLDERS).get().await() }
            val meetings = async {
                user.collection(MEETINGS).orderBy("startedAt", Query.Direction.DESCENDING).get().await()
            }
            val items = async { user.collection(ACTION_ITEMS).get().await() }

            val folderById = folders.await().documents.map { it.toFolderDto() }.associateBy { it.id }
            val pendingByMeeting = items.await().documents
                .map { it.toActionItemDto() }
                .filter { !it.done }
                .groupingBy { it.meetingId }
                .eachCount()

            meetings.await().documents.map { doc ->
                val dto = doc.toMeetingDto()
                dto.toDomain(
                    folder = dto.folderId?.let { folderById[it] },
                    pendingActionItems = pendingByMeeting[dto.id] ?: 0
                )
            }
        }
    }.mapDataFailure()

    override suspend fun getMeetingDetail(meetingId: String): Result<MeetingDetail> = suspendRunCatching {
        val user = userDoc()
        val meetingRef = user.collection(MEETINGS).document(meetingId)
        coroutineScope {
            val meetingSnap = async { meetingRef.get().await() }
            val transcript = async {
                meetingRef.collection(TRANSCRIPT_LINES).orderBy("startSeconds").get().await()
            }
            val items = async {
                user.collection(ACTION_ITEMS).whereEqualTo("meetingId", meetingId).get().await()
            }

            val snap = meetingSnap.await()
            if (!snap.exists()) throw DataException.NotFound()
            val meetingDto = snap.toMeetingDto()

            val folder = meetingDto.folderId?.let { folderId ->
                user.collection(FOLDERS).document(folderId).get().await()
                    .takeIf { it.exists() }?.toFolderDto()
            }

            val actionItems = items.await().documents
                .map { it.toActionItemDto() }
                .sortedBy { it.createdAt?.seconds ?: 0L }

            MeetingDetail(
                meeting = meetingDto.toDomain(folder, pendingActionItems = actionItems.count { !it.done }),
                transcript = transcript.await().documents.map { it.toTranscriptLineDto().toDomain() },
                actionItems = actionItems.map { it.toDomain() }
            )
        }
    }.mapDataFailure()

    override suspend fun getFolders(): Result<List<Folder>> = suspendRunCatching {
        val user = userDoc()
        coroutineScope {
            val folders = async { user.collection(FOLDERS).orderBy("createdAt").get().await() }
            val meetings = async { user.collection(MEETINGS).get().await() }

            val countByFolder = meetings.await().documents
                .mapNotNull { it.getString("folderId") }
                .groupingBy { it }
                .eachCount()

            folders.await().documents.map { doc ->
                val dto = doc.toFolderDto()
                dto.toDomain(meetingCount = countByFolder[dto.id] ?: 0)
            }
        }
    }.mapDataFailure()

    override suspend fun getUpcomingEvents(): Result<List<CalendarEvent>> = suspendRunCatching {
        // Lọc theo endsAt thì Firestore bắt buộc orderBy endsAt; sắp xếp lại theo giờ bắt đầu ở đây.
        userDoc().collection(CALENDAR_EVENTS)
            .whereGreaterThanOrEqualTo("endsAt", Timestamp.now())
            .orderBy("endsAt")
            .get().await()
            .documents
            .map { it.toCalendarEventDto().toDomain() }
            .sortedBy { it.startsAt }
    }.mapDataFailure()

    override suspend fun setActionItemDone(actionItemId: String, done: Boolean): Result<Unit> =
        suspendRunCatching {
            // Khi offline, lời gọi này chỉ hoàn tất sau khi máy chủ xác nhận; ghi chờ vẫn nằm trong
            // hàng đợi của Firestore và tự đồng bộ khi có mạng.
            userDoc().collection(ACTION_ITEMS).document(actionItemId).update("done", done).await()
            Unit
        }.mapDataFailure()

    private companion object {
        const val USERS = "users"
        const val FOLDERS = "folders"
        const val MEETINGS = "meetings"
        const val TRANSCRIPT_LINES = "transcriptLines"
        const val ACTION_ITEMS = "actionItems"
        const val CALENDAR_EVENTS = "calendarEvents"
    }
}
