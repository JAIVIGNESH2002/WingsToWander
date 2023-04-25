package com.example.wanderwings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val calibCard = findViewById<CardView>(R.id.calibCard)
        calibCard.setOnClickListener { val shakeActivity = Intent(this,ShakeCalibration::class.java)
        startActivity(shakeActivity)}
    }
}