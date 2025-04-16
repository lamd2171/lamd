package com.example.bitconintauto.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun prepareTessData(context: Context, dataPath: String, lang: String): Boolean {
        val tessDataDir = File("$dataPath/tessdata")
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs()
        }

        val file = File(tessDataDir, "$lang.traineddata")
        if (file.exists()) return true

        return try {
            val inputStream = context.assets.open("tessdata/$lang.traineddata")
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            inputStream.close()
            outputStream.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
