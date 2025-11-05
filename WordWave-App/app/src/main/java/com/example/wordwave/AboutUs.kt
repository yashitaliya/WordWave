package com.example.wordwave

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class AboutUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val btn_back = findViewById<ImageView>(R.id.btn_back)
        btn_back.setOnClickListener {
            finish()
        }
    }
}
