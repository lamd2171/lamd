package com.example.bitconintauto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.example.bitconintauto.ocr.TesseractManager
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    companion object {
        private const val REQUEST_MEDIA_PROJECTION_CODE = 1000
        private const val REQUEST_STORAGE_PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 초기화
        TesseractManager.init(applicationContext)
        PreferenceHelper.init(applicationContext)
        PermissionUtils.init(applicationContext)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        checkAndRequestStoragePermissions()

        btnStart.setOnClickListener {
            if (!PermissionUtils.isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                Toast.makeText(this, "접근성 권한을 설정해주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!PermissionUtils.checkOverlayPermission(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
                Toast.makeText(this, "오버레이 권한을 설정해주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            PermissionUtils.requestMediaProjection(this, REQUEST_MEDIA_PROJECTION_CODE)
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
            Toast.makeText(this, "작업 중지됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MEDIA_PROJECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val serviceIntent = Intent(this, com.example.bitconintauto.service.ForegroundProjectionService::class.java)
            serviceIntent.putExtra("code", resultCode)
            serviceIntent.putExtra("data", data)
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            Toast.makeText(this, "미디어 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
