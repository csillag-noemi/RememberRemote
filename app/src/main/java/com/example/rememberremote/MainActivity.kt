package com.example.rememberremote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {

    private lateinit var openButton: Button
    private lateinit var scanButton: Button
    private lateinit var qrScan: IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        openButton = findViewById(R.id.openButton)
        scanButton = findViewById(R.id.scanButton)
        qrScan = IntentIntegrator(this)

        scanButton.setOnClickListener {
            scanQRCode()
        }

        openButton.setOnClickListener {
            val intent = Intent(this, ControlsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun scanQRCode() {
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        qrScan.setPrompt("Scan a QR Code")
        qrScan.setCameraId(0) // Use the first back-facing camera
        qrScan.setOrientationLocked(true) // Lock the orientation to portrait
        qrScan.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                // Process the scan result
                processScanResult(result.contents)
            }
        }
    }

    private fun processScanResult(contents: String) {
        try {
            val jsonObject = JSONObject(contents)
            val ip = jsonObject.getString("ip")
            val port = jsonObject.getInt("port")

            Toast.makeText(this, "IP: $ip, Port: $port", Toast.LENGTH_LONG).show()
            sendMessage(ip, port, "Connected")
            // Here you can initiate the connection or pass the IP and port to another part of your app
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to parse QR code", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendMessage(ip: String, port: Int, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(ip, port).use { socket ->
                    PrintWriter(socket.getOutputStream(), true).use { out ->
                        out.println(message)
                    }
                }
            } catch (e: Exception) {

                Log.e("NetworkError", "Failed to send message", e)
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to send message: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}



