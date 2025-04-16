package com.example.bitconintauto.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.nio.ByteBuffer

object OCRCaptureUtils {
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaProjection: MediaProjection? = null
    private var screenWidth = 540
    private var screenHeight = 960
    private var screenDensity = 1

    private var tessBaseAPI: TessBaseAPI? = null

    fun initTesseract(context: Context) {
        val tess = TessBaseAPI()
        val dataPath = context.getExternalFilesDir(null)?.absolutePath + "/tesseract/"
        val lang = "eng"
        if (!FileUtil.prepareTessData(context, dataPath, lang)) {
            Log.e("OCR", "❌ Tesseract 데이터 준비 실패")
            return
        }
        tess.init(dataPath, lang)
        tess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789.")
        tessBaseAPI = tess
        Log.d("OCR", "✅ Tesseract 초기화 완료")
    }

    fun setMediaProjection(projection: MediaProjection) {
        Log.d("OCR", "✅ setMediaProjection 호출됨")
        mediaProjection = projection

        imageReader = ImageReader.newInstance(screenWidth, screenHeight, ImageFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "CaptureDisplay",
            screenWidth,
            screenHeight,
            screenDensity,
            0,
            imageReader?.surface,
            null,
            null
        )
    }

    fun captureSync(): Bitmap? {
        Log.d("OCR", "📸 captureSync() 진입")

        val image = imageReader?.acquireLatestImage()
        if (image == null) {
            Log.d("OCR", "⚠️ imageReader에서 이미지 가져오기 실패 (null)")
            return null
        }

        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        val pixelStride: Int = planes[0].pixelStride
        val rowStride: Int = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth

        val bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        Log.d("OCR", "✅ 이미지 캡처 성공")
        return bitmap
    }

    fun recognizeNumber(bitmap: Bitmap): String? {
        if (tessBaseAPI == null) {
            Log.e("OCR", "❌ Tesseract 인스턴스 없음. 초기화 누락됨.")
            return null
        }

        Log.d("OCR", "🧠 OCR 인식 시작")
        tessBaseAPI?.setImage(bitmap)
        val result = tessBaseAPI?.utF8Text?.trim()
        Log.d("OCR", "🔎 OCR 결과: $result")
        return result
    }
}
