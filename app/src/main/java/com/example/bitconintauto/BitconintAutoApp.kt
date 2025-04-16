package com.example.bitconintauto

import android.app.Application
import android.content.Context

class BitconintAutoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}
