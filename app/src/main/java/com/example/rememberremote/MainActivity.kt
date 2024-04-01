package com.example.rememberremote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : ComponentActivity() {

    private lateinit var scanButton: Button
    private lateinit var qrScan: IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        scanButton = findViewById(R.id.scanButton)
        qrScan = IntentIntegrator(this)

        scanButton.setOnClickListener {
            scanQRCode()
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

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        //commented ----- for TCP connection
/*        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val scannedData = result.contents
                val ipPortPattern = Regex("http://([^:]+):(\\d+)")    //bcs the app connects via URL
                val matchResult = ipPortPattern.matchEntire(scannedData)
                if (matchResult != null) {
                    val ipAddress = matchResult.groupValues[1]
                    val portNumber = matchResult.groupValues[2].toInt()
                    // Use the extracted IP address and port number to establish a connection with the desktop app
                    connectToDesktopApp(ipAddress, portNumber, generateUniqueId())
                } else {
                    Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }*/
    }

/*    private fun generateUniqueId(): String {
        // Generate a unique identifier for the Android app
        return UUID.randomUUID().toString()
    }*/

/*    private fun connectToDesktopApp(ipAddress: String, portNumber: Int, uniqueId: String) {
        // Create a new thread to perform the network operation
        thread(start = true) {
            // Create a new Socket and connect to the desktop app
            val socket = Socket()
            try {
                socket.connect(InetSocketAddress(ipAddress, portNumber), 5000)

                // Use the socket to send and receive data between the Android app and desktop app
                // Send a message to the desktop app to confirm that the connection has been established
                val message = "Connected: $uniqueId"
                val outputStream = socket.getOutputStream()
                val writer = BufferedWriter(OutputStreamWriter(outputStream))
                writer.write(message)
                writer.flush()

                // Close the socket
                socket.close()
            } catch (e: IOException) {
                // Handle the exception
                Log.e("MainActivity", "Error connecting to desktop app: ${e.message}")
            }
        }
    }*/
}



