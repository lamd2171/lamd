package com.example.bitconintauto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ui.FloatingController

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager

private lateinit var mediaProjectionManager: MediaProjectionManager
private var mediaProjection: MediaProjection? = null
private val REQUEST_SCREEN_CAPTURE = 1001

class MainActivity : AppCompatActivity() {

    private lateinit var controller: FloatingController
    private var isOverlayShown = false

    override fun onResume() {
        super.onResume()
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        tvStatus.text = if (MyAccessibilityService.instance != null) {
            "접근성 서비스: 활성화됨"
        } else {
            "접근성 서비스: 비활성화됨"
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // UI 연결
        val tvStatus = findViewById<TextView>(R.id.tv_status)
        val btnOpenSettings = findViewById<Button>(R.id.btn_open_settings)
        val btnStartOverlay = findViewById<Button>(R.id.btn_start_overlay)



        // 접근성 설정 화면으로 이동
        btnOpenSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        // 오버레이 띄우기
        btnStartOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "오버레이 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                return@setOnClickListener
            }

            if (!isOverlayShown) {
                controller = FloatingController(this)
                controller.show()
                isOverlayShown = true
            }
            mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
            startActivityForResult(captureIntent, REQUEST_SCREEN_CAPTURE)
        }
    }

    override fun onDestroy() {
        if (isOverlayShown) {
            controller.dismiss()
        }
        super.onDestroy()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREEN_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            if (mediaProjection != null) {
                // OCRCaptureUtils 에 전달
                com.example.bitconintauto.util.OCRCaptureUtils.setMediaProjection(mediaProjection!!)
                Toast.makeText(this, "화면 캡처 권한 허용됨", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
