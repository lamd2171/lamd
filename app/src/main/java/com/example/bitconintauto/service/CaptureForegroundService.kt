// app/src/main/java/com/example/bitconintauto/service/CaptureForegroundService.kt
package com.example.bitconintauto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.bitconintauto.R
import android.content.pm.ServiceInfo

class CaptureForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()

        val channelId = "capture_foreground_channel"
        val channelName = "Capture Foreground Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("화면 캡처 실행 중")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        // ✅ Android 12 이상에서는 projection 타입 명시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
