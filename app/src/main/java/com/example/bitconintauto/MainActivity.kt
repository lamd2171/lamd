package com.example.bitconintauto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceHelper.init(applicationContext)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        btnStart.setOnClickListener {
            if (!PermissionUtils.checkAndRequestOverlayPermission(this)) return@setOnClickListener
            if (!PermissionUtils.checkAndRequestMediaProjectionPermission(this)) return@setOnClickListener
            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                Toast.makeText(this, "접근성 권한을 활성화해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            OverlayView.show(this)
            ExecutorManager.start(this)
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
            OverlayView.remove(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            PermissionUtils.setMediaProjectionPermissionResult(resultCode, data)
            ScreenCaptureHelper.setUpProjection(this)
            Toast.makeText(this, "화면 캡처 권한 설정 완료", Toast.LENGTH_SHORT).show()
        }
    }
}
