package com.example.bitconintauto.service

import android.accessibilityservice.AccessibilityService
import android.widget.TextView
import com.example.bitconintauto.util.ClickSimulator
import kotlinx.coroutines.*
import com.example.bitconintauto.model.Coordinate

object ExecutorManager {
    private var isRunning = false
    private var job: Job? = null

    fun start(service: AccessibilityService, tvStatus: TextView) {
        if (isRunning) {
            tvStatus.append("\n이미 실행 중입니다")
            return
        }
        isRunning = true
        tvStatus.append("\n✅ 자동화 시작됨")

        val click = ClickSimulator(service)

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isRunning) {
                delay(2000)

                val triggerValue = 10 // 예시 값, 실제로는 OCR을 통한 값 처리 필요
                if (triggerValue >= 1) {
                    // 트리거 감지 후 실행 로그
                    withContext(Dispatchers.Main) {
                        tvStatus.append("\n[✅] 트리거 감지: $triggerValue 루틴 실행")
                    }
                    executeStepFlow(click, tvStatus)
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }

    // 단계별 실행 함수
    private suspend fun executeStepFlow(click: ClickSimulator, tvStatus: TextView) {
        // Step1 실행
        withContext(Dispatchers.Main) {
            tvStatus.append("\n[Step1] 클릭 작업 실행 중")
        }

        delay(500)

        // 예시로 Step2 작업을 실행
        withContext(Dispatchers.Main) {
            tvStatus.append("\n[Step2] 작업 완료")
        }
    }
}
