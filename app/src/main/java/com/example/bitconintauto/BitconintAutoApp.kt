package com.example.bitconintauto

import android.app.Application
import android.content.Context

class BitconintAutoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: BitconintAutoApp? = null
        val context: Context
            get() = instance!!.applicationContext
    }
}
