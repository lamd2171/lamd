package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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

        // ✅ 이전 권한 복원
        val projectionRestored = PermissionUtils.restoreMediaProjectionFromPreferences(this)
        val projection = PermissionUtils.getMediaProjection()
        val service = MyAccessibilityService.instance

        // 복원된 권한이 있으면 자동으로 루틴 실행
        if (projectionRestored && projection != null && service != null) {
            if (!overlayView.isAttached()) overlayView.show(this)
            executorManager.captureAndTriggerIfNeeded(this, overlayView, service)
        }

        // Start 버튼 클릭 시 권한 확인 후 프로젝션 요청
        btnStart.setOnClickListener {
            // Overlay 권한 확인
            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this, REQUEST_OVERLAY_PERMISSION)
                return@setOnClickListener
            }

            // 접근성 서비스 활성화 여부 확인
            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "접근성 서비스를 활성화해야 합니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            // MediaProjection 권한 요청
            PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
        }

        // Stop 버튼 클릭 시 실행 중인 자동화 루틴 중지
        btnStop.setOnClickListener {
            Log.d("Main", "🛑 자동화 정지 요청됨")
            executorManager.stop()
            overlayView.remove()
        }
    }

    // 권한 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // MediaProjection 권한 요청 결과 처리
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Main", "📸 MediaProjection 권한 획득")

            // 권한 결과 저장
            PermissionUtils.persistMediaProjectionPermission(applicationContext, resultCode, data)

            // Foreground 서비스 시작
            val serviceIntent = Intent(this, ForegroundProjectionService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
            }
            ContextCompat.startForegroundService(this, serviceIntent)

            // 일정 시간 후 루틴 실행
            Handler(mainLooper).postDelayed({
                if (!overlayView.isAttached()) overlayView.show(this)

                val projection = PermissionUtils.getMediaProjection()
                val service = MyAccessibilityService.instance

                if (projection != null && service != null) {
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
