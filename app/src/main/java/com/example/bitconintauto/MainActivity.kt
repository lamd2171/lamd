package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager

import com.example.bitconintauto.util.PreferenceHelper
import android.accessibilityservice.AccessibilityService

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView

    private val resultLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            MediaProjectionStarter.handlePermissionResult(this, result.resultCode, result.data!!, tvStatus)
        } else {
            Toast.makeText(this, "화면 캡처 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceHelper.init(applicationContext)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        tvStatus = findViewById(R.id.tvStatus)

        btnStart.setOnClickListener {
            val service = PreferenceHelper.accessibilityService
            if (service == null) {
                Toast.makeText(this, "접근성 서비스를 먼저 활성화해야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
                return@setOnClickListener
            }

            MediaProjectionStarter.requestCapturePermission(this, resultLauncher)
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
        }
    }
}
