package com.example.bitconintauto

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnReset: Button
    private lateinit var overlayView: OverlayView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 좌표 불러오기
        CoordinateManager.load(this)

        // OCR 캡처 초기화 (필요시 SurfaceView 전달 가능)
        val window = window
        val surface = findViewById<android.view.SurfaceView>(R.id.surfaceView) // 필요시 layout에 추가
        OCRCaptureUtils.init(window, surface)

        // 오버레이로 등록된 좌표 표시
        overlayView = OverlayView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.addView(overlayView, params)
        overlayView.setCoordinates(CoordinateManager.get("trigger") + CoordinateManager.get("step2") + CoordinateManager.get("step3")) // 예시로 표시

        // 버튼
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnReset = findViewById(R.id.btnReset)

        btnStart.setOnClickListener {
            ExecutorManager.start(this)
            Toast.makeText(this, "자동화 시작", Toast.LENGTH_SHORT).show()
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
            Toast.makeText(this, "자동화 중단", Toast.LENGTH_SHORT).show()
        }

        btnReset.setOnClickListener {
            CoordinateManager.reset()
            Toast.makeText(this, "좌표 초기화됨", Toast.LENGTH_SHORT).show()
        }

        // 권한 체크
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ExecutorManager.stop()
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.removeView(overlayView)
    }
}
