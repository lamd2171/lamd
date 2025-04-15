package com.example.bitconintauto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.PreferenceHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartOverlay: Button
    private lateinit var btnOpenSettings: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceHelper.init(applicationContext)

        tvStatus = findViewById(R.id.tv_status)
        btnStartOverlay = findViewById(R.id.btn_start_overlay)
        btnOpenSettings = findViewById(R.id.btn_open_settings)

        updateServiceStatus()

        btnOpenSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnStartOverlay.setOnClickListener {
            if (!isAccessibilityServiceEnabled(this)) {
                Toast.makeText(this, "접근성 서비스를 먼저 켜주세요", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            ExecutorManager.start(PreferenceHelper.accessibilityService!!)
            Toast.makeText(this, "오버레이 및 자동화 실행 시작", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateServiceStatus() {
        val isEnabled = isAccessibilityServiceEnabled(this)
        tvStatus.text = if (isEnabled) {
            "접근성 서비스 활성화됨"
        } else {
            "접근성 서비스 꺼짐"
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        return PreferenceHelper.accessibilityService != null
    }

    override fun onDestroy() {
        ExecutorManager.stop()
        super.onDestroy()
    }
}
