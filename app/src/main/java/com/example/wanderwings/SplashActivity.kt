package com.example.wanderwings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            val sharedPref = applicationContext.getSharedPreferences(
                "LOGIN_PREF",
                Context.MODE_PRIVATE
            )
                val auth = Firebase.auth
                val phoneNumber = sharedPref.getString("number",null)// Replace with the phone number you want to check
                val logFlag = sharedPref.getBoolean("aLogin",false)
                if (logFlag) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else{
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            finish()
        }, splashTimeOut)
    }
}