// PermissionUtils.kt
package com.example.bitconintauto.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

object PermissionUtils {
    private var resultCode: Int = -1
    private var dataIntent: Intent? = null

    fun checkOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}"))
        activity.startActivityForResult(intent, requestCode)
    }

    fun getProjectionManager(context: Context): MediaProjectionManager {
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

    fun checkMediaProjectionPermissionGranted(): Boolean {
        return resultCode == Activity.RESULT_OK && dataIntent != null
    }


    fun requestMediaProjection(activity: Activity, requestCode: Int) {
        val manager = getProjectionManager(activity)
        val intent = manager?.createScreenCaptureIntent()
        if (intent != null) {
            activity.startActivityForResult(intent, requestCode)
        }
    }


    fun setMediaProjectionPermissionResult(result: Int, intent: Intent?) {
        resultCode = result
        dataIntent = intent
    }

    fun getMediaProjectionPermissionData(): Intent? {
        return dataIntent
    }

    fun getMediaProjectionPermissionResultCode(): Int {
        return resultCode
    }

}
