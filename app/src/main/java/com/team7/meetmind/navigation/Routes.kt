package com.team7.meetmind.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"

    const val HOME = "home"
    const val FOLDERS = "folders"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"

    const val RECORDING = "recording"
    const val SEARCH = "search"

    const val MEETING_DETAIL = "meeting_detail/{meetingId}"
    fun meetingDetail(meetingId: String) = "meeting_detail/$meetingId"
}
