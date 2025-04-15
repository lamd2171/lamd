package com.example.bitconintauto.ocr

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class OCRProcessor {
    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    fun getText(bitmap: Bitmap?): String {
        var resultText = ""
        if (bitmap == null) return resultText

        val image = InputImage.fromBitmap(bitmap, 0)
        val task = recognizer.process(image)
            .addOnSuccessListener { result -> resultText = result.text }
            .addOnFailureListener { e -> Log.e("OCRProcessor", "OCR 실패", e) }

        while (!task.isComplete) Thread.sleep(10)
        return resultText
    }

    fun release() {
        recognizer.close()
    }
}
