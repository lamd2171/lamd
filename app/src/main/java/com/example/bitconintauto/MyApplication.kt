package com.example.bitconintauto

import android.app.Application
import com.example.bitconintauto.util.AppLogger

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.log("앱 시작됨")
    }
}
