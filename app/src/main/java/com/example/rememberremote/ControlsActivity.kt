package com.example.rememberremote

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.net.Socket
import java.util.Locale

class ControlsActivity : AppCompatActivity() {

    private lateinit var nextUpButton: Button
    private lateinit var nextDownButton: Button
    private lateinit var toggleButton: ToggleButton
    private lateinit var slider: Slider
    private lateinit var ip: String
    private var port: Int = 0
    private var prevVal: Float = 100.0F  //F for float value
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controls)

        nextUpButton = findViewById(R.id.nextUpButton)
        nextDownButton = findViewById(R.id.nextDownButton)
        toggleButton = findViewById(R.id.toggleButton)
        slider = findViewById(R.id.slider)

        ip = intent.getStringExtra("IP_ADDRESS") ?: ""
        port = intent.getIntExtra("PORT", 0)

        if (ip.isEmpty() || port == -1) {
            Toast.makeText(this, "Failed to receive IP address and port.", Toast.LENGTH_LONG).show()
            return
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            val command = if (isChecked) "MUSIC_ON" else "MUSIC_OFF"
            sendCommand(command)
        }

        slider.addOnChangeListener{ slider, value, fromUser ->
            // Send the slider value to the desktop app
            val trimmedVal = String.format(Locale.US, "%.2f", value).toFloat()
            if((trimmedVal != prevVal) && (count != 0 || trimmedVal != 0.0f)) {
                //var sendVal = trimmedVal * 10
                sendSliderValue(trimmedVal)
                prevVal = trimmedVal
                count = 1;
                Log.e("sent", trimmedVal.toString())
            }
        }
    }

    private fun sendSliderValue(value: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val message = "SLIDER_VALUE:$value"
                Socket(ip, port).use { socket ->
                    PrintWriter(socket.getOutputStream(), true).use { out ->
                        out.println(message)
                    }
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Failed to send command", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ControlsActivity,
                        "Failed to send command: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun sendCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(ip, port).use { socket ->
                    PrintWriter(socket.getOutputStream(), true).use { out ->
                        out.println(command)
                    }
                }
                runOnUiThread {
                    Toast.makeText(
                        this@ControlsActivity,
                        "Command sent: $command",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("NetworkError", "Failed to send command", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ControlsActivity,
                        "Failed to send command: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}