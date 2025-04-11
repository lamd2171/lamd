package com.example.bitconintauto

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.model.Coordinate
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
    private lateinit var viewCoordsButton: Button
    private lateinit var statusText: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var seekValueText: TextView
    private lateinit var debugModeSwitch: Switch
    private lateinit var floatingController: FloatingController

    private var overlayView: OverlayView? = null
    private var interval: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SharedPrefUtils.isFirstRun(this)) {
            val tutorial = TutorialOverlay(this)
            tutorial.show()
            SharedPrefUtils.setFirstRunComplete(this)
        }

        PreferenceHelper.init(this)
        CoordinateManager.init(this)
        CoordinateManager.loadFromPrefs(this)

        if (!PreferenceHelper.isTutorialSeen()) {
            showTutorialDialog()
        }

        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        } else {
            initOverlay()
        }

        floatingController = FloatingController(this)
        floatingController.show()

        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        resetButton = findViewById(R.id.btnReset)
        addCoordButton = findViewById(R.id.btnAddCoord)
        permissionButton = findViewById(R.id.btnPermission)
        viewCoordsButton = findViewById(R.id.btnViewCoords)
        debugModeSwitch = findViewById(R.id.switchDebugMode)
        statusText = findViewById(R.id.statusText)
        seekBar = findViewById(R.id.seekBar)
        seekValueText = findViewById(R.id.seekValueText)

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        startButton.setOnClickListener {
            statusText.text = "상태: 실행 중"
            startService(Intent(this, MyAccessibilityService::class.java))
        }

        stopButton.setOnClickListener {
            statusText.text = "상태: 정지됨"
            stopService(Intent(this, MyAccessibilityService::class.java))
        }

        resetButton.setOnClickListener {
            PreferenceHelper.clear(this)
            CoordinateManager.reset()
            CoordinateManager.saveToPrefs(this)
            statusText.text = "상태: 초기화 완료"
        }

        addCoordButton.setOnClickListener {
            val overlay = TouchCaptureOverlay(this) { x, y ->
                showCoordinateTypeDialog(x, y)
            }
            overlay.show()
        }

        viewCoordsButton.setOnClickListener {
            startActivity(Intent(this, CoordinateListActivity::class.java))
        }

        val isDebugEnabled = PreferenceHelper.getString(this, "debug_mode") == "true"
        debugModeSwitch.isChecked = isDebugEnabled
        debugModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            PreferenceHelper.saveString(this, "debug_mode", isChecked.toString())
            Toast.makeText(this, "디버그 모드: ${if (isChecked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }

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

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this) && overlayView == null) {
            initOverlay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoordinateManager.removeCoordinateChangedListener()
        floatingController.dismiss()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.removeView(overlayView)
    }

    private fun initOverlay() {
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

        CoordinateManager.setOnCoordinateChangedListener {
            overlayView?.setCoordinates(CoordinateManager.get("primary"))
        }

        if (CoordinateManager.isFirstClick()) {
            val guide = FirstClickGuideOverlay(this)
            guide.show {}
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
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

    private fun showCoordinateTypeDialog(x: Int, y: Int) {
        val coordTypes = arrayOf("primary", "click", "copy", "paste", "final")

        AlertDialog.Builder(this)
            .setTitle("좌표 용도 선택")
            .setItems(coordTypes) { _, which ->
                val selectedType = coordTypes[which]
                showLabelInputDialog(x, y, selectedType)
            }
            .setCancelable(true)
            .show()
    }

    private fun showLabelInputDialog(x: Int, y: Int, type: String) {
        val input = EditText(this).apply {
            hint = "예: 복사 위치"
        }

        AlertDialog.Builder(this)
            .setTitle("라벨 입력")
            .setView(input)
            .setPositiveButton("확인") { _, _ ->
                val label = input.text.toString()
                val coord = Coordinate(x, y, label = label)
                CoordinateManager.append(type, coord)
                overlayView?.setCoordinates(CoordinateManager.get(type))
                Toast.makeText(this, "$type 좌표 등록됨: ($x, $y) 라벨: $label", Toast.LENGTH_SHORT).show()
                CoordinateManager.saveToPrefs(this)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
