package com.example.rememberremote

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        val scanButton = findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener {
            val intent = Intent(this, ScanPage::class.java)
            startActivity(intent)
        }
    }

}



