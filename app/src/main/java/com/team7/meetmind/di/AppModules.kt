package com.team7.meetmind.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team7.meetmind.data.repository.AuthRepositoryImpl
import com.team7.meetmind.data.repository.MeetingRepositoryImpl
import com.team7.meetmind.domain.repository.AuthRepository
import com.team7.meetmind.domain.repository.MeetingRepository
import com.team7.meetmind.domain.usecase.auth.LoginUseCase
import com.team7.meetmind.domain.usecase.auth.LogoutUseCase
import com.team7.meetmind.domain.usecase.auth.ObserveSessionUseCase
import com.team7.meetmind.domain.usecase.auth.RegisterUseCase
import com.team7.meetmind.domain.usecase.auth.SendPasswordResetEmailUseCase
import com.team7.meetmind.domain.usecase.meeting.GetFoldersUseCase
import com.team7.meetmind.domain.usecase.meeting.GetMeetingDetailUseCase
import com.team7.meetmind.domain.usecase.meeting.GetMeetingsUseCase
import com.team7.meetmind.domain.usecase.meeting.GetUpcomingEventsUseCase
import com.team7.meetmind.domain.usecase.meeting.SearchMeetingsUseCase
import com.team7.meetmind.domain.usecase.meeting.SetActionItemDoneUseCase
import com.team7.meetmind.presentation.auth.ForgotPasswordViewModel
import com.team7.meetmind.presentation.auth.LoginViewModel
import com.team7.meetmind.presentation.auth.RegisterViewModel
import com.team7.meetmind.presentation.calendar.CalendarViewModel
import com.team7.meetmind.presentation.folders.FoldersViewModel
import com.team7.meetmind.presentation.home.HomeViewModel
import com.team7.meetmind.presentation.meetingdetail.MeetingDetailViewModel
import com.team7.meetmind.presentation.recording.RecordingViewModel
import com.team7.meetmind.presentation.search.SearchViewModel
import com.team7.meetmind.presentation.session.SessionViewModel
import com.team7.meetmind.presentation.settings.SettingsViewModel
import com.team7.meetmind.presentation.theme.ThemePreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Tầng data: Firebase (Auth + Firestore) và các repository (singleton, dùng chung cả app). */
val dataModule = module {
    single { ThemePreferences(androidContext()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<MeetingRepository> { MeetingRepositoryImpl(get(), get()) }
}

/** Tầng domain: mỗi use case một lớp nhỏ, tạo mới khi cần (factory). */
val domainModule = module {
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveSessionUseCase)
    factoryOf(::SendPasswordResetEmailUseCase)

    factoryOf(::GetMeetingsUseCase)
    factoryOf(::GetMeetingDetailUseCase)
    factoryOf(::SearchMeetingsUseCase)
    factoryOf(::GetFoldersUseCase)
    factoryOf(::GetUpcomingEventsUseCase)
    factoryOf(::SetActionItemDoneUseCase)
}

/** Tầng presentation: ViewModel do Koin tạo và gắn với vòng đời màn hình. */
val presentationModule = module {
    viewModelOf(::SessionViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::RecordingViewModel)
    viewModelOf(::MeetingDetailViewModel) // nhận meetingId qua parametersOf(...)
    viewModelOf(::FoldersViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::SettingsViewModel)
}

val appModules = listOf(dataModule, domainModule, presentationModule)
