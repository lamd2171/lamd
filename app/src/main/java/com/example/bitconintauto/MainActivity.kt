import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.util.CoordinateManager
import com.example.bitconintauto.util.OCRCaptureUtils
import com.example.bitconintauto.ui.OverlayView

class MainActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start button logic
        startButton = findViewById(R.id.start_button)
        startButton.setOnClickListener {
            OCRCaptureUtils.initTesseract(applicationContext)
            OverlayView.show(applicationContext)
            startAutomation()
        }

        // Stop button logic
        stopButton = findViewById(R.id.stop_button)
        stopButton.setOnClickListener {
            OverlayView.remove(applicationContext)
            handler.removeCallbacksAndMessages(null) // Stop scheduled tasks
        }
    }

    private fun startAutomation() {
        handler.post(object : Runnable {
            override fun run() {
                val bitmap = OCRCaptureUtils.captureSync()
                if (bitmap != null) {
                    val result = OCRCaptureUtils.recognizeNumber(bitmap)
                    if (result != null) {
                        OverlayView.getInstance()?.updateText("OCR Result: $result")
                    }
                }
                handler.postDelayed(this, 1000) // Run every second
            }
        })
    }
}
