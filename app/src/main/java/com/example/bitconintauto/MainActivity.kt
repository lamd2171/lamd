// [12] app/src/main/java/com/example/bitconintauto/MainActivity.kt

package com.example.bitconintauto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
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

    private val REQUEST_OVERLAY_PERMISSION = 1001
    private val REQUEST_MEDIA_PROJECTION = 1002

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
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission()
                return@setOnClickListener
            }

            if (!isAccessibilityServiceEnabled(this)) {
                Toast.makeText(this, "접근성 서비스를 먼저 켜주세요", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                return@setOnClickListener
            }

            val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
            startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION)
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "오버레이 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "오버레이 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            MediaProjectionStarter.handlePermissionResult(this, resultCode, data)
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