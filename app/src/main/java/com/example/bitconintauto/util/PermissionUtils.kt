package com.example.bitconintauto.util

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import android.util.Log

object PermissionUtils {
    private var resultCode: Int = -1
    private var resultData: Intent? = null

    fun checkAndRequestMediaProjectionPermission(activity: Activity, requestCode: Int) {
        val projectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = projectionManager.createScreenCaptureIntent()
        activity.startActivityForResult(permissionIntent, requestCode)
    }

    fun setMediaProjectionPermissionResult(code: Int, data: Intent?) {
        Log.d("Main", "📸 setMediaProjectionPermissionResult 저장됨")
        resultCode = code
        resultData = data
    }

    fun getMediaProjection(context: Context): MediaProjection? {
        val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return if (resultCode == RESULT_OK && resultData != null) {
            projectionManager.getMediaProjection(resultCode, resultData!!)
        } else null
    }

    fun checkOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, requestCode)
    }
}
