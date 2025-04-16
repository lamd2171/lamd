package com.example.bitconintauto.util

import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import com.example.bitconintauto.BitconintAutoApp
import com.example.bitconintauto.ui.OverlayView

object ScreenCaptureHelper {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    fun setUpProjection(context: android.content.Context) {
        val resultCode = PreferenceHelper.resultCode
        val resultData = PreferenceHelper.resultData
        val manager = context.getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
        mediaProjection = manager.getMediaProjection(resultCode, resultData!!)
        Log.d("ScreenCapture", "setUpProjection() 완료")
        OverlayView.updateDebugText("미디어 프로젝션 준비 완료")
    }

    fun captureSync(): Bitmap? {
        val context = BitconintAutoApp.appContext
        val wm = context.getSystemService(WindowManager::class.java)
        val display = wm.defaultDisplay
        val width = display.width
        val height = display.height

        imageReader = ImageReader.newInstance(width, height, 0x1, 2)

        if (mediaProjection == null) {
            Log.e("ScreenCapture", "mediaProjection is NULL!")
            OverlayView.updateDebugText("캡처 실패: mediaProjection == null")
            return null
        }

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width, height, context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, Handler(Looper.getMainLooper())
        )

        Thread.sleep(300)

        val image = imageReader?.acquireLatestImage()
        if (image == null) {
            Log.e("ScreenCapture", "acquireLatestImage() → NULL")
            OverlayView.updateDebugText("캡처 실패: 이미지 null")
            return null
        }

        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width

            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            Log.d("ScreenCapture", "Bitmap 캡처 성공")
            OverlayView.updateDebugText("Bitmap 캡처 성공")
            return bitmap
        } catch (e: Exception) {
            Log.e("ScreenCapture", "Bitmap 생성 오류: ${e.message}")
            OverlayView.updateDebugText("Bitmap 오류: ${e.message}")
            return null
        } finally {
            image.close()
        }
    }
}
