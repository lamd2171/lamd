package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.ForegroundProjectionService
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var overlayView: OverlayView
    private lateinit var executorManager: ExecutorManager

    companion object {
        const val REQUEST_MEDIA_PROJECTION = 1001
        const val REQUEST_OVERLAY_PERMISSION = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionUtils.init(applicationContext)
        PreferenceHelper.init(applicationContext)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        overlayView = OverlayView(this)
        executorManager = ExecutorManager()

        val projectionRestored = PermissionUtils.restoreMediaProjectionFromPreferences(this)
        val projection = PermissionUtils.getMediaProjection()
        val service = MyAccessibilityService.instance

        if (projectionRestored && projection != null && service != null) {
            overlayView.show(this)
            executorManager.captureAndTriggerIfNeeded(this, overlayView, service)
        }

        btnStart.setOnClickListener {
            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this, REQUEST_OVERLAY_PERMISSION)
                return@setOnClickListener
            }

            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "접근성 서비스를 활성화해야 합니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
        }

        btnStop.setOnClickListener {
            Log.d("Main", "🛑 자동화 정지 요청됨")
            executorManager.stop()
            overlayView.remove()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Main", "📸 MediaProjection 권한 획득")
            PermissionUtils.persistMediaProjectionPermission(applicationContext, resultCode, data)

            val serviceIntent = Intent(this, ForegroundProjectionService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
            }
            ContextCompat.startForegroundService(this, serviceIntent)

            Handler(Looper.getMainLooper()).postDelayed({
                val projection = PermissionUtils.getMediaProjection()
                val service = MyAccessibilityService.instance
                if (projection != null && service != null) {
                    if (!overlayView.isAttached()) overlayView.show(this)
                    executorManager.captureAndTriggerIfNeeded(this, overlayView, service)
                } else {
                    Toast.makeText(this, "MediaProjection 또는 접근성 서비스가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }, 500)
        } else {
            Toast.makeText(this, "MediaProjection 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
