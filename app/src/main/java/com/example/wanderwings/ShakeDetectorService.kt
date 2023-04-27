package com.example.wanderwings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.math.abs

class ShakeDetectorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f
    private var lastUpdate = 0L

    private val NOTIFICATION_ID = 1

    // Notification channel ID required for Android O and higher
    private val CHANNEL_ID = "MyAppNotificationChannel"

    private lateinit var notificationManager:NotificationManager



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // Notification Manager instance
         notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Initialize SensorManager and accelerometer
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Check if app has permission to access accelerometer
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            Toast.makeText(
                this,
                "App does not have permission to access the accelerometer",
                Toast.LENGTH_SHORT
            ).show()
            stopSelf()
        } else {
            // Register accelerometer listener if permission granted
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Create a notification channel (for Android O and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My App Notification Channel"
            val description = "Notification Channel for My App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister accelerometer listener
        sensorManager.unregisterListener(this)
    }

    // Handle shake detection
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            Log.i("yup","shake")
            // Calculate acceleration and check if it's above threshold
            val acceleration = calculateAcceleration(event)
            Log.i("acc",isShake(acceleration).toString()+" "+acceleration)

            // Intent for "OK" button click action
            val okIntent = Intent(this, MainActivity    ::class.java)
            okIntent.action = "OK_ACTION"
            val okPendingIntent = PendingIntent.getActivity(this, 0, okIntent, PendingIntent.FLAG_UPDATE_CURRENT)

// Intent for "Cancel" button click action
            val cancelIntent = Intent(this, NotificationBroadCastReceiver::class.java)
            cancelIntent.action = "CANCEL_ACTION"
            val cancelPendingIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)


            if (isShake(acceleration)) {
                val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Are you safe? Heavy motion detected")
                    .setContentText("Detected a heavy movement are you safe?")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true).setOngoing(true)
                    .setSmallIcon(R.drawable.app_icon_3)
                    .addAction(R.drawable.ic_flash, "Yes", cancelPendingIntent)
                    .addAction(R.drawable.ic_info, "No", okPendingIntent)

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
// Start a 10 second countdown timer
                object : CountDownTimer(10000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        // Update the notification text with the remaining time
                        val secondsRemaining = millisUntilFinished / 1000
                        notificationBuilder.setContentText("SOS will be sent in $secondsRemaining seconds")
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                    }

                    override fun onFinish() {
                        // Update the notification text with the final message
                        notificationBuilder.setContentText("SOS is being sent")
                        notificationBuilder.setAutoCancel(true)
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())


                    }
                }.start()            }
            lastX = acceleration
            lastUpdate = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    // Calculate acceleration
    private fun calculateAcceleration(event: SensorEvent): Float {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        return abs(x + y + z - lastX - lastY - lastZ) / (event.timestamp - lastUpdate) * 10000
    }

    // Check if shake is detected
    private fun isShake(acceleration: Float): Boolean {
        return acceleration > 12E-10
    }
}
