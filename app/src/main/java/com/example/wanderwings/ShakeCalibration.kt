package com.example.wanderwings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.math.sqrt

class ShakeCalibration : AppCompatActivity() , SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var  progressBar:ProgressBar
    private lateinit var gTv:TextView
    private lateinit var calButton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_calibration)

         gTv = findViewById<TextView>(R.id.gForce)
        calButton = findViewById(R.id.calButton)
         progressBar = findViewById<ProgressBar>(R.id.calProgress)

        // Get the system's SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Get the device's accelerometer sensor
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Get the app's shared preferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        Log.i("sPut",sharedPreferences.getFloat("gForce",0f).toString())
    }
    override fun onResume() {
        super.onResume()

        // Register the sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener
        sensorManager.unregisterListener(this)
    }
    var tGforce = 0f
    override fun onSensorChanged(event: SensorEvent) {
        // Check if the event is from the accelerometer sensor
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Calculate the G-force
            val gForce = calculateGForce(event.values[0], event.values[1], event.values[2])
            val rootView = findViewById<View>(android.R.id.content)
            gTv.setText(gForce.toString())
            if(gForce>tGforce) {
                tGforce = gForce
                progressBar.setProgress(gForce.toInt()*10)
                if(gForce>5 && gForce<8){
                    progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.prime))
                    Snackbar.make(rootView, "Not enough GForce, shake harder", Snackbar.LENGTH_LONG).show()
                }else if(gForce>8) {
                    calButton.setText("CALIBRATE ✔")
                    calButton.setBackgroundColor(ContextCompat.getColor(this,R.color.okgreen))
                    Log.i("sPut", gForce.toString())
                    progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.okgreen))
                    // Store the G-force in shared preferences
                    with(sharedPreferences.edit()) {
                        putFloat("gForce", gForce)
                        apply()
                    }
                    //Vibrate
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(500)
                    }

                    sensorManager.unregisterListener(this)
                }else{
                    progressBar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.secureBlue))
                }
            }else {
                tGforce = gForce
            }

        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
    private fun calculateGForce(x: Float, y: Float, z: Float): Float {
        val gravity = SensorManager.GRAVITY_EARTH
        val alpha = 0.8f

        // Calculate the acceleration values without gravity
        val xAcceleration = x - gravity
        val yAcceleration = y - gravity
        val zAcceleration = z - gravity

        // Calculate the G-force
        val gForce = sqrt((xAcceleration * xAcceleration) + (yAcceleration * yAcceleration) + (zAcceleration * zAcceleration)) / gravity

        // Apply a low-pass filter to smooth the values
        return alpha * gForce + (1 - alpha) * sharedPreferences.getFloat("gForce", 0f)
    }

}
