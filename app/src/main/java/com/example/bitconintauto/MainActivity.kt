package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.ForegroundProjectionService
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.service.MyAccessibilityService
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var overlayView: OverlayView
    private lateinit var executorManager: ExecutorManager

    companion object {
        const val REQUEST_MEDIA_PROJECTION = 1001
        const val REQUEST_OVERLAY_PERMISSION = 1002
        const val REQUEST_ACCESSIBILITY_SETTINGS = 1003
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

            val serviceIntent = Intent(this, ForegroundProjectionService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
                Log.e("main", " ForegroundService 전달완료.")
            }
            ContextCompat.startForegroundService(this, serviceIntent)

            Handler(mainLooper).postDelayed({
                if (!overlayView.isAttached()) {
                    overlayView.show(this)  // 또는 applicationContext
                }

                val projection = PermissionUtils.getMediaProjection()
                if (projection == null) {
                    Log.e("Main", "⛔ MediaProjection 아직 준비 안 됨. 루틴 실행 취소.")
                    Toast.makeText(this, "화면 캡처 권한 초기화 대기 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    return@postDelayed
                }

                val service = MyAccessibilityService.instance
                if (service != null) {
                    executorManager.captureAndTriggerIfNeeded(this, overlayView, service)
                } else {
                    Toast.makeText(this, "접근성 서비스가 아직 활성화되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }, 500)
        } else {
            Toast.makeText(this, "MediaProjection 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
