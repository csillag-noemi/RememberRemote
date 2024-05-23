package com.example.rememberremote

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.net.Socket

class ControlsActivity : AppCompatActivity() {

    private lateinit var nextUpButton: Button
    private lateinit var nextDownButton: Button
    private lateinit var toggleButton: ToggleButton
    private lateinit var ip: String
    private var port: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controls)

        nextUpButton = findViewById(R.id.nextUpButton)
        nextDownButton = findViewById(R.id.nextDownButton)
        toggleButton = findViewById(R.id.toggleButton)

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