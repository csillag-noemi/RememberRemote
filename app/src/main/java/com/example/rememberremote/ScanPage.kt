package com.example.rememberremote


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScanPage : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_page)

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        val btnScan = findViewById<Button>(R.id.btn_scan)
        scannerView = findViewById(R.id.camera_preview)

        btnScan.setOnClickListener {
            // Start scanning when the button is clicked
            scannerView.setResultHandler(this)
            scannerView.startCamera()
        }
    }

    fun handleResult(result: Result?) {
        // Handle the result here (e.g., display the QR code content)
        val qrCodeContent = result?.text ?: "No QR Code detected"
        // TODO: Handle the QR code content as needed

        // Stop the camera after scanning
        scannerView.stopCamera()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }




}