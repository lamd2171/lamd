package com.example.bitconintauto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.MyAccessibilityService
import com.example.bitconintauto.util.PreferenceHelper

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SharedPrefUtils.isFirstRun(this)) {
            val tutorial = TutorialOverlay(this)
            tutorial.show()
            SharedPrefUtils.setFirstRunComplete(this)
        }

        PreferenceHelper.init(this)

        if (!PreferenceHelper.isTutorialSeen()) {
            showTutorialDialog()
        }

        // View 초기화
        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        resetButton = findViewById(R.id.btnReset)
        addCoordButton = findViewById(R.id.btnAddCoord)
        permissionButton = findViewById(R.id.btnPermission)
        statusText = findViewById(R.id.statusText)
        seekBar = findViewById(R.id.seekBar)
        seekValueText = findViewById(R.id.seekValueText)

        // 접근성 권한 버튼
        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        // 시작 버튼
        startButton.setOnClickListener {
            statusText.text = "상태: 실행 중"
            val intent = Intent(this, MyAccessibilityService::class.java)
            startService(intent)
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

        // 좌표 추가
        addCoordButton.setOnClickListener {
            Toast.makeText(this, "좌표 등록 기능이 곧 활성화됩니다.", Toast.LENGTH_SHORT).show()
        }

        // 실행 주기 설정
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
