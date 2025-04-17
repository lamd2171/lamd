package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var executorManager: ExecutorManager
    private lateinit var overlayView: OverlayView

    companion object {
        const val REQUEST_MEDIA_PROJECTION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 버튼 초기화
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        // 오버레이 뷰 및 실행 매니저 초기화
        overlayView = OverlayView(this)
        executorManager = ExecutorManager(this, overlayView)

        btnStart.setOnClickListener {
            if (!PermissionUtils.checkOverlayPermission(this)) {
                PermissionUtils.requestOverlayPermission(this)
                return@setOnClickListener
            }

            if (!PermissionUtils.checkMediaProjectionPermissionGranted()) {
                PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION)
            } else {
                startAutomation()
            }
        }

        btnStop.setOnClickListener {
            executorManager.stop()
            overlayView.removeFromWindow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            PermissionUtils.setMediaProjectionPermissionResult(resultCode, data)
            startAutomation()
        } else {
            Toast.makeText(this, "화면 캡처 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startAutomation() {
        Log.d("MainActivity", "▶ 자동화 시작됨")
        executorManager.start()
    }
}
