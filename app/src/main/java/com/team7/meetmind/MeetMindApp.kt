package com.team7.meetmind

import android.app.Application
import com.team7.meetmind.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MeetMindApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MeetMindApp)
            modules(appModules)
        }
    }
}
