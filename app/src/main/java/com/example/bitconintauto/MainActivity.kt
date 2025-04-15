// MainActivity.kt
package com.example.bitconintauto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.OCRCaptureUtils

class MainActivity : AppCompatActivity() {

    private val REQUEST_MEDIA_PROJECTION = 1001
    private var mediaProjectionManager: MediaProjectionManager? = null
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        btnStart.setOnClickListener {
            val intent = mediaProjectionManager?.createScreenCaptureIntent()
            startActivityForResult(intent, REQUEST_MEDIA_PROJECTION)
        }

        btnStop.setOnClickListener {
            ExecutorManager.stop()
            Toast.makeText(this, "자동화 중지됨", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK && data != null) {
            val projection = mediaProjectionManager?.getMediaProjection(resultCode, data)
            if (projection != null) {
                OCRCaptureUtils.setMediaProjection(projection)
                ExecutorManager.start(applicationContext)
            } else {
                Toast.makeText(this, "미디어 프로젝션 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
