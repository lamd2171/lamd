package com.example.bitconintauto.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.bitconintauto.R

object SoundPlayer {

    private var soundPool: SoundPool? = null
    private var soundId: Int = 0
    private var isLoaded = false

    fun init(context: Context) {
        if (soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool!!.load(context, R.raw.notify_sound, 1)
        soundPool!!.setOnLoadCompleteListener { _, _, status ->
            isLoaded = status == 0
        }
    }

    fun play() {
        if (isLoaded) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
