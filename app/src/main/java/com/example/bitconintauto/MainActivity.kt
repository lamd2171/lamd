package com.example.bitconintauto

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.ui.FloatingController
import com.example.bitconintauto.ui.OverlayView
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.SharedPrefUtils

class MainActivity : AppCompatActivity() {

    private lateinit var overlayView: OverlayView
    private lateinit var floatingController: FloatingController
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var permissionButton: Button
    private lateinit var addCoordButton: Button
    private lateinit var debugSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 오버레이 권한 확인
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }

        // 초기 설정값 불러오기
        SharedPrefUtils.init(this)
        CoordinateManager.loadFromPrefs(this)

        // 오버레이 뷰 추가
        overlayView = OverlayView(this)
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        wm.addView(overlayView, params)

        // 플로팅 컨트롤러
        floatingController = FloatingController(this)
        floatingController.show()

        // 버튼 초기화
        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        permissionButton = findViewById(R.id.btnPermission)
        addCoordButton = findViewById(R.id.btnAddCoord)
        debugSwitch = findViewById(R.id.switchDebug)

        startButton.setOnClickListener {
            ExecutorManager.start(this)
        }

        stopButton.setOnClickListener {
            ExecutorManager.stop()
        }

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        addCoordButton.setOnClickListener {
            Toast.makeText(this, "좌표 등록 기능 준비 중", Toast.LENGTH_SHORT).show()
        }

        debugSwitch.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefUtils.setDebugMode(isChecked)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingController.dismiss()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(overlayView)
    }
}
