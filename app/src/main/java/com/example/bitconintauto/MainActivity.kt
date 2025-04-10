package com.example.bitconintauto

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var inputButton: Button
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var intervalSeekBar: SeekBar
    private lateinit var intervalText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // 반드시 findViewById 전에 있어야 함

        registerButton = findViewById(R.id.btnRegister)
        inputButton = findViewById(R.id.btnInputValue)
        startButton = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)
        resetButton = findViewById(R.id.btnReset)
        intervalSeekBar = findViewById(R.id.intervalSeekBar)
        intervalText = findViewById(R.id.intervalText)
        statusText = findViewById(R.id.statusText)

        intervalSeekBar.max = 60
        intervalSeekBar.progress = 10
        intervalText.text = "주기: 10초"

        intervalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                intervalText.text = "주기: ${progress}초"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        registerButton.setOnClickListener {
            Toast.makeText(this, "좌표 등록", Toast.LENGTH_SHORT).show()
        }

        inputButton.setOnClickListener {
            Toast.makeText(this, "기준값 입력", Toast.LENGTH_SHORT).show()
        }

        startButton.setOnClickListener {
            statusText.text = "상태: 실행 중"
        }

        stopButton.setOnClickListener {
            statusText.text = "상태: 중지됨"
        }

        resetButton.setOnClickListener {
            statusText.text = "상태: 초기화됨"
        }
    }
}
