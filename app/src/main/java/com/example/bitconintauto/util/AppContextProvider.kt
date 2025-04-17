package com.example.bitconintauto.util

import android.annotation.SuppressLint
import android.content.Context

object AppContextProvider {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    fun init(appContext: Context) {
        context = appContext
    }

    fun getContext(): Context {
        if (!::context.isInitialized) {
            throw IllegalStateException("AppContextProvider not initialized. Call init() in Application or MainActivity.")
        }
        return context
    }
}
