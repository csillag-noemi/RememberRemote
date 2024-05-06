package com.example.rememberremote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.rememberremote.network.ApiClient
import com.example.rememberremote.network.ApiService
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private lateinit var scanButton: Button
    private lateinit var qrScan: IntentIntegrator
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Initialize the scan button and QR scanner
        scanButton = findViewById(R.id.scanButton)
        qrScan = IntentIntegrator(this)
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        qrScan.setPrompt("Scan a QR Code")
        qrScan.setCameraId(0) // Use a specific camera
        qrScan.setOrientationLocked(true) // Lock the orientation to portrait

        scanButton.setOnClickListener {
            qrScan.initiateScan() // Start the QR code scan
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show()
            } else {
                // Display the scan result
                Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()

                // Ensure the scanned URL ends with a slash for Retrofit compatibility
                val baseUrl = result.contents.ifBlank { return }.let {
                    if (!it.endsWith("/")) "$it/" else it
                }

                // Initialize the API service with the base URL from the QR code
                apiService = ApiClient.getApiService(baseUrl)

                // Send a "Connected" message to the desktop application
                sendConnectedMessage()
            }
        }
    }

    private fun sendConnectedMessage() {
        Log.d("ApiClient", "Sending 'Connected' message to: ${apiService.toString()}")
        apiService.sendConnected("Connected").enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Log the error and show an error message
                Log.e("ApiClient", "Failed to connect: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to connect: ${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle the successful response
                    val responseBody = response.body()?.string()
                    Log.d("ApiClient", "Success: Server responded with: $responseBody")
                    Toast.makeText(this@MainActivity, "Server Response: $responseBody", Toast.LENGTH_LONG).show()
                } else {
                    // Log detailed error response
                    Log.e("ApiClient", "Error response code: ${response.code()}, Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
