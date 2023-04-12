package com.example.wanderwings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        val editTextName = findViewById<TextInputEditText>(R.id.userName)
        val editTextMobile = findViewById<TextInputEditText>(R.id.userMobile)

        val registerButton = findViewById<Button>(R.id.regitserButton)

        //Registration details
        val enteredName = editTextName.text.toString()


        registerButton.setOnClickListener {
            val enteredMobile = editTextMobile.text.toString()
            val startVerification = Intent(this,numberverification::class.java)
            startVerification.putExtra("verifyNumber","+91"+enteredMobile)
            startActivity(startVerification)
            finish()
        }


    }
}