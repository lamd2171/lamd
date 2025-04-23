package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper
import com.example.bitconintauto.service.ForegroundProjectionService


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_OVERLAY = 1000
        const val REQUEST_MEDIA_PROJECTION = 1001
    }

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var overlayView: OverlayView

    private val executorManager = ExecutorManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        overlayView = findViewById(R.id.overlay_container)  // XML에서 정의된 OverlayView 사용

        PermissionUtils.init(applicationContext)
        PreferenceHelper.init(applicationContext)

        btnStart.setOnClickListener {
            Log.d("MainActivity", "Start Button Clicked") // 시작 버튼 클릭 로그

            // 1. 오버레이 권한 체크
            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this, REQUEST_OVERLAY)
                return@setOnClickListener
            }

            // 2. 접근성 서비스 활성화 확인
            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "접근성 서비스를 먼저 활성화해주세요", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            // 3. MediaProjection 권한 확인
            if (!PermissionUtils.checkMediaProjectionPermissionGranted()) {
                PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
                return@setOnClickListener
            }

            val projection = PermissionUtils.getMediaProjection()
            if (projection == null) {
                Toast.makeText(this, "MediaProjection 객체 없음", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("MainActivity", "MediaProjection 객체 확인됨") // MediaProjection 확인 로그

            // 4. MediaProjection 설정
            ScreenCaptureHelper.setMediaProjection(projection)

            Log.d("MainActivity", "ExecutorManager 시작됨") // ExecutorManager 시작 로그

            // 5. ExecutorManager 시작
            executorManager.start(
                context = this,
                overlayView = overlayView,
                service = MyAccessibilityService.instance!!
            )
        }


        btnStop.setOnClickListener {
            // 자동화 중지
            executorManager.stop()
            Toast.makeText(this, "자동화 중지", Toast.LENGTH_SHORT).show()
        }
    }

    // MediaProjection 권한 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // MediaProjection 권한이 승인된 후 ForegroundProjectionService로 전달
                Log.d("MainActivity", "MediaProjection 권한 승인됨")

                Intent(this, ForegroundProjectionService::class.java).apply {
                    putExtra("code", resultCode)
                    putExtra("data", data)
                    startForegroundService(this)
                }
            } else {
                // 권한이 승인되지 않은 경우
                Log.e("MainActivity", "❌ MediaProjection 권한 거부됨")
            }
        }
    }

}
