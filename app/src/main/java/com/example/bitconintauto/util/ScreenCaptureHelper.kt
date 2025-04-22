package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null

    fun setMediaProjection(projection: MediaProjection) {
        mediaProjection = projection
    }

    fun getMediaProjection(): MediaProjection? {
        return mediaProjection
    }

    // ✅ 캡처 시작 전 MediaProjection 유효성 검사
    fun captureScreen(context: Context, projection: MediaProjection): Bitmap? {
        if (projection == null) {
            Log.e("ScreenCaptureHelper", "❌ MediaProjection is null")
            return null
        }
        return try {
            capture(context, projection)
        } catch (e: Exception) {
            Log.e("ScreenCaptureHelper", "❌ 예외 발생: ${e.message}")
            null
        }
    }

    // ✅ 공통 캡처 로직
    // MediaProjection을 캡처하기 전에 callback을 등록해야 함
    private fun capture(context: Context, projection: MediaProjection): Bitmap? {
        val width: Int
        val height: Int
        val density: Int

        val displayMetrics = context.resources.displayMetrics
        density = displayMetrics.densityDpi

        // 화면 크기 설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            val bounds = windowMetrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(metrics)
            width = metrics.widthPixels
            height = metrics.heightPixels
        }

        // 이미지 리더 생성
        val imageReader = ImageReader.newInstance(width, height, 0x1, 2)

        // MediaProjection 콜백 등록
        val projectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Log.d("ScreenCaptureHelper", "❌ MediaProjection stopped")
            }
        }

        // VirtualDisplay 생성 전에 callback을 등록
        projection.registerCallback(projectionCallback, Handler(Looper.getMainLooper()))

        // VirtualDisplay 생성
        val virtualDisplay: VirtualDisplay = projection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            Handler(Looper.getMainLooper())
        )

        Thread.sleep(1000) // 안정성 확보

        // 이미지를 획득
        val image = imageReader.acquireLatestImage()
        if (image == null) {
            Log.e("ScreenCaptureHelper", "❌ 이미지 획득 실패 (null)")
            virtualDisplay.release()
            return null
        }

        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width

        // Bitmap 생성
        val bitmap = Bitmap.createBitmap(
            width + rowPadding / pixelStride,
            height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        virtualDisplay.release()
        Log.d("Capture", "🖼️ 캡처된 이미지 해상도: ${bitmap.width}x${bitmap.height}")
        return Bitmap.createBitmap(bitmap, 0, 0, width, height)
    }
}
