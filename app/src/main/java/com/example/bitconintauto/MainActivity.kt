package com.example.bitconintauto

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
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

    private var interval: Int = 3 // 기본값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View 초기화
        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        resetButton = findViewById(R.id.btnReset)
        addCoordButton = findViewById(R.id.btnAddCoord)
        permissionButton = findViewById(R.id.btnPermission)
        statusText = findViewById(R.id.statusText)
        seekBar = findViewById(R.id.seekBar)
        seekValueText = findViewById(R.id.seekValueText)

        // SeekBar 동작
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

        // 접근성 권한 열기 버튼
        permissionButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        // 자동화 시작
        startButton.setOnClickListener {
            statusText.text = "상태: 실행 중"

            val serviceIntent = Intent(this, MyAccessibilityService::class.java)
            startService(serviceIntent)
        }

        // 자동화 중지
        stopButton.setOnClickListener {
            statusText.text = "상태: 정지됨"
            stopService(Intent(this, MyAccessibilityService::class.java))
        }

        // 초기화 버튼
        resetButton.setOnClickListener {
            PreferenceHelper.clear(this)
            statusText.text = "상태: 초기화 완료"
        }

        // 좌표 추가 (자동화 앱의 로직에 따라 구현 필요)
        addCoordButton.setOnClickListener {
            // 예: 좌표 등록 화면으로 전환 또는 좌표 감지 UI 활성화
            statusText.text = "좌표 등록 모드"
        }
    }
}
