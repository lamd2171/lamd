package com.example.bitconintauto.util

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.widget.TextView
import com.example.bitconintauto.service.ExecutorManager

object MediaProjectionStarter {

    fun requestCapturePermission(activity: Activity, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val mpm = activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mpm.createScreenCaptureIntent()
        launcher.launch(intent)
    }

    fun handlePermissionResult(activity: Activity, resultCode: Int, data: Intent, statusView: TextView) {
        val mpm = activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mpm.getMediaProjection(resultCode, data)
        ScreenCaptureHelper.init(projection, activity.applicationContext)
        val service = PreferenceHelper.accessibilityService ?: return
        ExecutorManager.start(service, statusView)
    }
}
