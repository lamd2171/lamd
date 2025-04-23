package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
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
    private var overlayView: OverlayView? = null
    private val executorManager = ExecutorManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        PermissionUtils.init(applicationContext)
        PreferenceHelper.init(applicationContext)

        btnStart.setOnClickListener {
            Log.d("MainActivity", "▶️ Start Button Clicked")

            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this, REQUEST_OVERLAY)
                return@setOnClickListener
            }

            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "접근성 서비스를 활성화 해주세요", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            if (!PermissionUtils.checkMediaProjectionPermissionGranted()) {
                PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
                return@setOnClickListener
            }

            val projection = PermissionUtils.getMediaProjection()
            if (projection == null) {
                Toast.makeText(this, "MediaProjection 객체 없음", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ScreenCaptureHelper.setMediaProjection(projection)
            addOverlayView()

            executorManager.start(
                context = this,
                overlayView = overlayView!!,
                service = MyAccessibilityService.instance!!
            )
        }

        btnStop.setOnClickListener {
            executorManager.stop()
            removeOverlayView()
            Toast.makeText(this, "⏹️ 자동화 중지", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOverlayView() {
        if (overlayView != null) return
        overlayView = OverlayView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlayView, params)
        Log.d("MainActivity", "🟢 OverlayView 추가됨")
    }

    private fun removeOverlayView() {
        overlayView?.let {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.removeView(it)
            overlayView = null
            Log.d("MainActivity", "🔴 OverlayView 제거됨")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.d("MainActivity", "✅ MediaProjection 권한 승인됨")

                Intent(this, ForegroundProjectionService::class.java).apply {
                    putExtra("code", resultCode)
                    putExtra("data", data)
                    startForegroundService(this)
                }
            } else {
                Log.e("MainActivity", "❌ MediaProjection 권한 거부됨")
            }
        }
    }
}
