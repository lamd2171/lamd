package com.example.bitconintauto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bitconintauto.R

class ForegroundProjectionService : Service() {

    companion object {
        const val CHANNEL_ID = "projection_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("자동화 실행 중")
            .setContentText("화면 캡처 및 자동 루틴 실행 중입니다.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        Log.d("ForegroundService", "🟢 ForegroundProjectionService 시작됨")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 이 서비스는 단순히 MediaProjection 권한 보장을 위한 용도임
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ForegroundService", "🛑 ForegroundProjectionService 종료됨")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "화면 캡처 서비스",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
