package com.example.bitconintauto

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView  // 상태를 표시할 TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        tvStatus = findViewById(R.id.tvStatus)  // TextView 연결

        // 오버레이 권한이 없으면 설정으로 유도
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(intent)
            Toast.makeText(this, "오버레이 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
        }

        btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val service = PreferenceHelper.accessibilityService
                if (service is android.accessibilityservice.AccessibilityService) {
                    ExecutorManager.start(service, tvStatus)
                }
                if (service != null) {
                    ExecutorManager.start(service, tvStatus)
                } else {
                    Toast.makeText(this, "접근성 서비스를 먼저 켜주세요.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            } else {
                Toast.makeText(this, "오버레이 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
        }
    }

    // 로그를 화면에 출력하는 함수
    private fun logToScreen(message: String) {
        val currentText = tvStatus.text.toString()
        tvStatus.text = "$currentText\n$message"
    }
}
