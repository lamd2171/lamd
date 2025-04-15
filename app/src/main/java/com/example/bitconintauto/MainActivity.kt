package com.example.bitconintauto

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceHelper.init(applicationContext)

        tvStatus = findViewById(R.id.tv_status)
        btnStart = findViewById(R.id.btn_start_overlay)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            // 권한 체크 및 오버레이 시작
            if (Settings.canDrawOverlays(this)) {
                startAutomation()
            } else {
                tvStatus.text = "오버레이 권한을 승인해야 합니다."
            }
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
            tvStatus.text = "자동화가 종료되었습니다."
        }
    }

    private fun startAutomation() {
        tvStatus.text = "자동화 시작 중..."
        ExecutorManager.start() // 실제 자동화 루틴 시작
    }
}
