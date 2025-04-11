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
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.ui.*
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.SharedPrefUtils

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var addCoordButton: Button
    private lateinit var permissionButton: Button
    private lateinit var statusText: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var seekValueText: TextView

    private var interval: Int = 3

    private lateinit var overlayView: OverlayView
    private lateinit var floatingController: FloatingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 최초 튜토리얼
        if (SharedPrefUtils.isFirstRun(this)) {
            val tutorial = TutorialOverlay(this)
            tutorial.show()
            SharedPrefUtils.setFirstRunComplete(this)
        }

        PreferenceHelper.init(this)

        if (!PreferenceHelper.isTutorialSeen()) {
            showTutorialDialog()
        }

        // 오버레이 권한 체크
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder(this)
                .setTitle("권한 요청")
                .setMessage("다른 앱 위에 표시 권한이 필요합니다.")
                .setPositiveButton("설정으로 이동") { _, _ ->
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                }
                .setNegativeButton("취소", null)
                .show()
        }

        // OverlayView 초기화 및 등록
        overlayView = OverlayView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(overlayView, params)

        // 좌표 변경 시 자동으로 OverlayView 갱신
        CoordinateManager.setOnCoordinateChangedListener {
            overlayView.setCoordinates(CoordinateManager.get("primary"))
        }

        // 최초 좌표 안내 오버레이
        if (CoordinateManager.isFirstClick()) {
            val guide = FirstClickGuideOverlay(this)
            guide.show {}
        }

        // 플로팅 버튼 표시
        floatingController = FloatingController(this)
        floatingController.show()

        // View 초기화
        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        resetButton = findViewById(R.id.btnReset)
        addCoordButton = findViewById(R.id.btnAddCoord)
        permissionButton = findViewById(R.id.btnPermission)
        statusText = findViewById(R.id.statusText)
        seekBar = findViewById(R.id.seekBar)
        seekValueText = findViewById(R.id.seekValueText)

        // 접근성 권한 이동
        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // 시작 버튼
        startButton.setOnClickListener {
            statusText.text = "상태: 실행 중"
            startService(Intent(this, MyAccessibilityService::class.java))
        }

        // 중지 버튼
        stopButton.setOnClickListener {
            statusText.text = "상태: 정지됨"
            stopService(Intent(this, MyAccessibilityService::class.java))
        }

        // 초기화 버튼
        resetButton.setOnClickListener {
            PreferenceHelper.clear(this)
            statusText.text = "상태: 초기화 완료"
        }

        // 좌표 추가 버튼 (개선 예정)
        addCoordButton.setOnClickListener {
            Toast.makeText(this, "좌표 등록 기능이 곧 활성화됩니다.", Toast.LENGTH_SHORT).show()
        }

        // 주기 설정
        seekBar.max = 10
        seekBar.progress = interval
        seekValueText.text = "${interval}초"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                interval = if (value == 0) 1 else value
                seekValueText.text = "${interval}초"
                PreferenceHelper.saveString(this@MainActivity, "cycle_interval", interval.toString())
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        CoordinateManager.removeCoordinateChangedListener()
        floatingController.dismiss()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(overlayView)
    }

    private fun showTutorialDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tutorial, null)
        val checkbox = dialogView.findViewById<CheckBox>(R.id.checkboxDontShowAgain)

        AlertDialog.Builder(this)
            .setTitle("앱 사용 안내")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                if (checkbox.isChecked) {
                    PreferenceHelper.setTutorialSeen(true)
                }
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
