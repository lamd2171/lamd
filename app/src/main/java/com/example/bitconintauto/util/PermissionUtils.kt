// PermissionUtils.kt
package com.example.bitconintauto.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

object PermissionUtils {
    private lateinit var appContext: Context
    private var resultCode: Int = -1
    private var dataIntent: Intent? = null
    private var mediaProjection: MediaProjection? = null

    /**
     * 앱 초기화
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * 오버레이 권한 확인
     */
    fun checkOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * 오버레이 권한 요청
     */
    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * 미디어 프로젝션 매니저 가져오기
     */
    private fun getProjectionManager(context: Context): MediaProjectionManager {
        return context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    /**
     * 접근성 서비스 활성화 확인
     */
    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val expectedComponentName = "${context.packageName}/${serviceClass.name}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val servicesList = enabledServices.split(":")
        return servicesList.contains(expectedComponentName)
    }

    /**
     * 미디어 프로젝션 권한이 승인되었는지 확인
     */
    fun checkMediaProjectionPermissionGranted(): Boolean {
        return resultCode == Activity.RESULT_OK && dataIntent != null
    }

    /**
     * 미디어 프로젝션 권한 요청
     */
    fun requestMediaProjection(activity: Activity, requestCode: Int) {
        // 이미 MediaProjection이 저장되어 있으면 다시 요청하지 않음
        if (mediaProjection == null) {
            val manager = getProjectionManager(activity)
            val intent = manager.createScreenCaptureIntent()
            activity.startActivityForResult(intent, requestCode)
        } else {
            Log.d("PermissionUtils", "✅ 이미 승인된 MediaProjection 사용")
        }
    }

    /**
     * 미디어 프로젝션 권한 결과 설정
     */
    fun setMediaProjectionPermissionResult(result: Int, intent: Intent?) {
        resultCode = result
        dataIntent = intent
        val manager = getProjectionManager(appContext)
        mediaProjection = manager.getMediaProjection(resultCode, dataIntent!!)
        Log.d("PermissionUtils", "✅ MediaProjection 객체 저장됨")
    }

    /**
     * 저장된 MediaProjection 반환
     */
    fun getMediaProjection(): MediaProjection? {
        return mediaProjection
    }

    /**
     * MediaProjection 권한 최초 승인 시 저장
     */
    fun persistMediaProjectionPermission(context: Context, resultCode: Int, data: Intent) {
        val prefs = context.getSharedPreferences("ProjectionPrefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("resultCode", resultCode)
            .putString("intentUri", data.toUri(0))
            .apply()
    }

    /**
     * 앱 재실행 시 저장된 MediaProjection 복원
     */
    fun restoreMediaProjectionFromPreferences(context: Context): Boolean {
        val prefs = context.getSharedPreferences("ProjectionPrefs", Context.MODE_PRIVATE)
        val savedCode = prefs.getInt("resultCode", -1)
        val uri = prefs.getString("intentUri", null)

        if (savedCode != Activity.RESULT_OK || uri == null) return false
        val intent = Intent.parseUri(uri, 0)

        setMediaProjectionPermissionResult(savedCode, intent)
        return true
    }
}
