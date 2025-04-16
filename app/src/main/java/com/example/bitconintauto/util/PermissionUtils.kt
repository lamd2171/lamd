package com.example.bitconintauto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

object PermissionUtils {

    fun checkAndRequestOverlayPermission(activity: Activity): Boolean {
        if (!Settings.canDrawOverlays(activity)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = android.net.Uri.parse("package:" + activity.packageName)
            activity.startActivity(intent)
            Toast.makeText(activity, "오버레이 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun checkAndRequestMediaProjectionPermission(activity: Activity): Boolean {
        if (PreferenceHelper.resultData == null) {
            val mgr = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
            activity.startActivityForResult(mgr.createScreenCaptureIntent(), 1001)
            return false
        }
        return true
    }

    fun setMediaProjectionPermissionResult(resultCode: Int, data: Intent?) {
        PreferenceHelper.resultCode = resultCode
        PreferenceHelper.resultData = data
    }

    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val expected = "${context.packageName}/${serviceClass.name}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.split(":").contains(expected)
    }
}
