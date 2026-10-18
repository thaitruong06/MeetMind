package com.team7.meetmind.ui.common

import androidx.compose.ui.graphics.Color
import com.team7.meetmind.ui.theme.SignalAmber
import com.team7.meetmind.ui.theme.SignalCoral
import com.team7.meetmind.ui.theme.SignalViolet
import com.team7.meetmind.ui.theme.StateSuccess
import com.team7.meetmind.ui.theme.TextMuted

/**
 * Các model và dữ liệu mẫu dùng để dựng giao diện (chưa nối với Room/Firestore/API thật).
 * Khi làm Giai đoạn 4 trong timeline, thay các hàm đọc dữ liệu này bằng Repository/ViewModel thật,
 * nhưng giữ nguyên các Composable — chúng chỉ phụ thuộc vào các data class bên dưới.
 */
data class Meeting(
    val id: String,
    val title: String,
    val dateLabel: String,
    val durationLabel: String,
    val folder: String,
    val tags: List<String>,
    val summary: String,
    val participants: List<String>,
    val pendingActionItems: Int
)

data class TranscriptLine(val speaker: String, val time: String, val text: String)

data class ActionItemUi(
    val id: String,
    val content: String,
    val assignee: String,
    val due: String,
    val done: Boolean
)

data class FolderUi(val name: String, val meetingCount: Int, val color: Color)

data class CalendarEventUi(
    val id: String,
    val title: String,
    val timeLabel: String,
    val dateLabel: String,
    val willAutoRecord: Boolean
)

object SampleData {

    val folders = listOf(
        FolderUi("Dự án MeetMind", 6, SignalAmber),
        FolderUi("Khách hàng", 3, SignalCoral),
        FolderUi("Kỹ thuật", 4, SignalViolet),
        FolderUi("Cá nhân", 1, StateSuccess)
    )

    val meetings = listOf(
        Meeting(
            id = "m1",
            title = "Họp kickoff dự án MeetMind",
            dateLabel = "Hôm nay, 09:00",
            durationLabel = "42 phút",
            folder = "Dự án MeetMind",
            tags = listOf("kickoff", "ưu tiên cao"),
            summary = "Nhóm thống nhất phạm vi tính năng giai đoạn 1 và chốt công nghệ Kotlin, Compose, Firebase.",
            participants = listOf("T", "P"),
            pendingActionItems = 3
        ),
        Meeting(
            id = "m2",
            title = "Rà soát thiết kế giao diện",
            dateLabel = "Hôm qua, 15:30",
            durationLabel = "28 phút",
            folder = "Dự án MeetMind",
            tags = listOf("UI/UX"),
            summary = "Thống nhất bảng màu vàng-cam-tím và hiệu ứng sóng âm cho màn hình xác thực.",
            participants = listOf("T", "P"),
            pendingActionItems = 1
        ),
        Meeting(
            id = "m3",
            title = "Họp khách hàng demo tiến độ",
            dateLabel = "15/10, 14:00",
            durationLabel = "55 phút",
            folder = "Khách hàng",
            tags = listOf("demo", "quan trọng"),
            summary = "Khách hàng góp ý thêm chức năng nhắc lịch họp tự động, nhóm ghi nhận bổ sung vào backlog.",
            participants = listOf("T", "P"),
            pendingActionItems = 2
        ),
        Meeting(
            id = "m4",
            title = "Đồng bộ tiến độ API",
            dateLabel = "12/10, 10:00",
            durationLabel = "20 phút",
            folder = "Kỹ thuật",
            tags = listOf("API", "backend"),
            summary = "Hoàn tất API xác thực, chuẩn bị bắt đầu API ghi âm và phiên âm.",
            participants = listOf("T"),
            pendingActionItems = 0
        )
    )

    val transcript = listOf(
        TranscriptLine("Thái", "00:00", "Chào mọi người, mình bắt đầu cuộc họp kickoff nhé."),
        TranscriptLine("Phú", "00:12", "Ok, mình đã chuẩn bị xong bản thiết kế giao diện sơ bộ."),
        TranscriptLine("Thái", "00:40", "Phần backend mình sẽ tập trung vào API xác thực trước."),
        TranscriptLine("Phú", "01:15", "Vậy tuần sau mình demo thử màn hình đăng nhập nhé."),
        TranscriptLine("Thái", "01:32", "Đồng ý, mình sẽ chuẩn bị tài liệu API song song.")
    )

    val actionItems = listOf(
        ActionItemUi("a1", "Hoàn thiện giao diện đăng nhập/đăng ký", "Phú", "20/10/2026", false),
        ActionItemUi("a2", "Chuẩn bị tài liệu API xác thực", "Thái", "21/10/2026", false),
        ActionItemUi("a3", "Lên lịch demo tuần sau", "Cả nhóm", "25/10/2026", true)
    )

    val calendarEvents = listOf(
        CalendarEventUi("e1", "Họp đồng bộ tiến độ tuần", "09:00 - 09:30", "Thứ Hai, 19/10", true),
        CalendarEventUi("e2", "Demo tính năng ghi âm", "14:00 - 15:00", "Thứ Tư, 21/10", true),
        CalendarEventUi("e3", "Họp khách hàng", "10:00 - 11:00", "Thứ Sáu, 23/10", false)
    )

    fun meetingById(id: String): Meeting = meetings.firstOrNull { it.id == id } ?: meetings.first()

    fun folderColor(name: String): Color = folders.firstOrNull { it.name == name }?.color ?: TextMuted
}
