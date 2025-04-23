package com.example.bitconintauto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.provider.Settings
import android.util.Log

object PermissionUtils {
    private lateinit var appContext: Context
    var mediaProjectionData: Intent? = null
    private var resultCode: Int = -1
    private var mediaProjection: MediaProjection? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun checkOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, requestCode)
    }

    private fun getProjectionManager(context: Context): MediaProjectionManager {
        return context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val expectedComponentName = "${context.packageName}/${serviceClass.name}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val servicesList = enabledServices.split(":")
        return servicesList.contains(expectedComponentName)
    }

    fun requestMediaProjection(activity: Activity, requestCode: Int) {
        if (mediaProjection == null) {
            val manager = getProjectionManager(activity)
            val intent = manager.createScreenCaptureIntent()
            activity.startActivityForResult(intent, requestCode)
        } else {
            Log.d("PermissionUtils", "✅ 이미 승인된 MediaProjection 사용")
        }
    }

    fun setMediaProjectionPermissionResult(result: Int, intent: Intent?) {
        resultCode = result
        mediaProjectionData = intent
        val manager = getProjectionManager(appContext)
        mediaProjection = manager.getMediaProjection(resultCode, mediaProjectionData!!)
        Log.d("PermissionUtils", "✅ MediaProjection 객체 저장됨")
    }

    fun getMediaProjection(): MediaProjection? {
        return mediaProjection
    }
}
