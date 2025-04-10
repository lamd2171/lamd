package com.example.bitconintauto.util

import android.content.Context
import android.media.MediaPlayer
import com.example.bitconintauto.R

object SoundPlayer {

    fun playSuccess(context: Context) {
        MediaPlayer.create(context, R.raw.success_sound)?.start()
    }

    fun playError(context: Context) {
        MediaPlayer.create(context, R.raw.error_sound)?.start()
    }

    fun playNotify(context: Context) {
        MediaPlayer.create(context, R.raw.notify_sound)?.start()
    }
}
