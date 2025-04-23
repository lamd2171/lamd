package com.example.bitconintauto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bitconintauto.R
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper

class ForegroundProjectionService : Service() {

    private val executorManager = ExecutorManager()
    private var overlayView: OverlayView? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("code", -1) ?: return START_NOT_STICKY
        val data = intent.getParcelableExtra<Intent>("data") ?: return START_NOT_STICKY

        // 💥 MediaProjection 전에 ForegroundService 시작
        startForeground(1, createNotification())

        // ✅ MediaProjection 저장
        PermissionUtils.setMediaProjectionPermissionResult(resultCode, data)
        ScreenCaptureHelper.setMediaProjection(PermissionUtils.getMediaProjection()!!)  // ← 핵심 추가

        // ✅ PreferenceHelper 다시 초기화 (ApplicationContext 사용)
        PreferenceHelper.init(applicationContext)

        // ✅ 자동화 OCR 루틴 진입
        val accessibilityService = MyAccessibilityService.instance
        if (accessibilityService != null) {
            // 💡 오버레이 뷰 생성 및 전달
            overlayView = OverlayView(applicationContext)
            executorManager.start(
                context = applicationContext,
                overlayView = overlayView!!,
                service = accessibilityService
            )
            Log.d("ForegroundService", "🟢 자동 실행 루틴 시작됨")
        } else {
            Log.e("ForegroundService", "❌ 접근성 서비스 인스턴스가 null입니다")
        }

        Log.d("ForegroundService", "🟢 ForegroundProjectionService 시작됨")
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "bitconintauto_channel"
        val channelName = "BitconintAuto OCR Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("자동화 실행 중")
            .setContentText("OCR 기반 자동 매크로 작동 중입니다.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
