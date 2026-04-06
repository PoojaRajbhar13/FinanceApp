package com.example.projectz

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
