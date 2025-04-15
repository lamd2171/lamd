package com.example.bitconintauto

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        tvStatus = findViewById(R.id.tvStatus)

        // ActivityResultLauncher 초기화
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                startAutomation(getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityService)
            }
        }

        // 오버레이 권한 요청
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            resultLauncher.launch(intent)
        }

        val accessibilityService = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityService

        btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startAutomation(accessibilityService)
            } else {
                Toast.makeText(this, "오버레이 권한을 승인해야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
        }
    }

    private fun startAutomation(service: AccessibilityService) {
        ExecutorManager.start(service, tvStatus)
    }
}
