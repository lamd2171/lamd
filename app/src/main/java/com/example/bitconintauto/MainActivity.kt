package com.example.bitconintauto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.bitconintauto.ui.FloatingController

class MainActivity : AppCompatActivity() {

    private lateinit var controller: FloatingController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        controller = FloatingController(this)
        controller.show()
    }

    override fun onDestroy() {
        controller.dismiss()
        super.onDestroy()
    }
}
