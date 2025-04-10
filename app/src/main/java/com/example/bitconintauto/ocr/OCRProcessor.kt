package com.example.bitconintauto.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ImageReader
import android.os.Environment
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.WindowManager
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File

class OcrProcessor(private val context: Context) {

    private val tessBaseApi: TessBaseAPI = TessBaseAPI()

    init {
        val tessDataPath = "${context.getExternalFilesDir(null)?.absolutePath}/tesseract/"
        val lang = "eng"

        val trainedDataFile = File("$tessDataPath/tessdata/$lang.traineddata")
        if (!trainedDataFile.exists()) {
            trainedDataFile.parentFile?.mkdirs()
            // 일반적으로 assets에서 복사하는 로직 필요
        }

        tessBaseApi.init(tessDataPath, lang)
    }

    fun recognizeNumberAt(x: Int, y: Int): Double {
        // 화면 캡처에서 OCR 처리할 영역을 잡고 숫자를 인식하는 기능 (간단화됨)
        val bitmap = captureScreenRegion(x, y, 100, 50) // 예시 크기
        return try {
            tessBaseApi.setImage(bitmap)
            val result = tessBaseApi.utF8Text.trim().replace("[^0-9.]".toRegex(), "")
            result.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun captureScreenRegion(x: Int, y: Int, width: Int, height: Int): Bitmap {
        // 실제 앱에서는 PixelCopy 또는 MediaProjection을 활용해야 함
        // 여기선 예시로 흰색 배경 Bitmap 생성
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
}
