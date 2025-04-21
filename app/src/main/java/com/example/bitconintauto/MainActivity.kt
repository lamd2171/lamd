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
import com.example.bitconintauto.util.ScreenCaptureHelper
import android.os.Handler
import android.os.Looper
import android.graphics.Rect


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

        // SharedPreference 초기화
        PreferenceHelper.init(applicationContext)

        // 버튼 연결
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        // 오버레이 뷰 및 실행 관리자 초기화
        overlayView = OverlayView(this)
        executorManager = ExecutorManager()

        btnStart.setOnClickListener {
            // 오버레이 권한 확인
            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this, REQUEST_OVERLAY_PERMISSION)
                return@setOnClickListener
            }

            // 접근성 서비스 확인
            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "접근성 서비스를 활성화해야 합니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }


            // MediaProjection 권한 요청
            PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
        }

        btnStop.setOnClickListener {
            Log.d("Main", "🛑 자동화 정지 요청됨")
            executorManager.stop()
            overlayView.remove()
        }
    }

    // 권한 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val mediaProjection = PermissionUtils.getMediaProjection()

        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Main", "📸 MediaProjection 권한 획득")

            // ✅ 먼저 MediaProjection을 ForegroundService로 전달
            val serviceIntent = Intent(this, ForegroundProjectionService::class.java).apply {
                putExtra("code", resultCode)
                putExtra("data", data)
                Log.e("main", " ForegroundService 전달완료.")
            }
            ContextCompat.startForegroundService(this, serviceIntent)

            // ✅ 0.5초 후 루틴 실행 (서비스가 MediaProjection을 세팅할 시간 확보)
            Handler(mainLooper).postDelayed({
                if (!overlayView.isAttached) {
                    Log.e("main", " MediaProjection 세팅시간 확보.")
                    overlayView.show()
                }

                // 🔽 MediaProjection 준비 여부 선확인
                val projection = PermissionUtils.getMediaProjection()
                if (projection == null) {
                    Log.e("Main", "⛔ MediaProjection 아직 준비 안 됨. 루틴 실행 취소.")
                    Toast.makeText(this, "화면 캡처 권한 초기화 대기 중입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    return@postDelayed
                }

               // ScreenCaptureHelper.setMediaProjection(projection)

                val testBitmap = ScreenCaptureHelper.captureScreen(this, Rect(0, 0, 540, 900))
                if (testBitmap == null) {
                    Log.e("Main", "⛔ 캡처 실패: bitmap == null")
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
