package com.example.bitconintauto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.PermissionUtils
import com.example.bitconintauto.util.PreferenceHelper
import com.example.bitconintauto.util.ScreenCaptureHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView  // tvStatus TextView 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 초기화
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        tvStatus = findViewById(R.id.tvStatus)  // tvStatus 참조

        // 시작 버튼 클릭 시
        btnStart.setOnClickListener {
            if (!PermissionUtils.checkAndRequestOverlayPermission(this)) {
                return@setOnClickListener
            }

            if (!PermissionUtils.checkAndRequestMediaProjectionPermission(this)) {
                return@setOnClickListener
            }

            // 권한 요청 후 자동화 시작
            ExecutorManager.start(applicationContext, tvStatus)

            tvStatus.text = "자동화 시작 중..." // 상태 업데이트
            Toast.makeText(this, "자동화가 시작되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 정지 버튼 클릭 시
        btnStop.setOnClickListener {
            ExecutorManager.stop()
            tvStatus.text = "자동화 중지됨"  // 상태 업데이트
            Toast.makeText(this, "자동화가 중지되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 요청 후 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            PermissionUtils.setMediaProjectionPermissionResult(resultCode, data)
            if (resultCode == RESULT_OK) {
                tvStatus.text = "권한 허용됨"
            } else {
                tvStatus.text = "권한 거부됨"
            }
        }
    }
}
