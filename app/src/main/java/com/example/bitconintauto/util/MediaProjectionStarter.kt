package com.example.bitconintauto.util

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import com.example.bitconintauto.service.ExecutorManager
import com.example.bitconintauto.util.ScreenCaptureHelper


object MediaProjectionStarter {
    fun handlePermissionResult(activity: Activity, resultCode: Int, data: Intent) {
        val mpm = activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val projection = mpm.getMediaProjection(resultCode, data)
        ScreenCaptureHelper.init(projection, activity.applicationContext)

        val service = PreferenceHelper.accessibilityService
        if (service != null) {
            ExecutorManager.start(service)
        }
    }
}
