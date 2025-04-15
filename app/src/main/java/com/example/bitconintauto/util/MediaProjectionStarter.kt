package com.example.bitconintauto.util

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.widget.TextView
import com.example.bitconintauto.service.ExecutorManager
import android.accessibilityservice.AccessibilityService

object MediaProjectionStarter {
    fun requestCapturePermission(activity: Activity, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val mpm = activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mpm.createScreenCaptureIntent()
        launcher.launch(captureIntent)
    }

    fun handlePermissionResult(activity: Activity, resultCode: Int, data: Intent, tvStatus: TextView) {
        val mpm = activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mpm.getMediaProjection(resultCode, data)
        ScreenCaptureHelper.init(projection, activity.applicationContext)

        val service = PreferenceHelper.accessibilityService
        if (service != null) {
            ExecutorManager.start(service, tvStatus)
        } else {
            tvStatus.text = "❌ 접근성 서비스가 연결되지 않았습니다"
        }
    }
}
