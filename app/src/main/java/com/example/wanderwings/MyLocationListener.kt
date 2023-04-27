package com.example.wanderwings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MyLocationListener(private val context: Context) : LocationListener {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun requestLocationUpdates() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, start requesting location updates
            startRequestingLocationUpdates()
        } else {
            // Permission is not granted, ask the user for permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRequestingLocationUpdates() {
        // Check if location provider is enabled
        if (isLocationProviderEnabled()) {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                this
            )
        } else {
            // Show dialog to user indicating that location provider is disabled
            // and direct them to the device settings to enable it
        }
    }

    fun stopRequestingLocationUpdates() {
        locationManager.removeUpdates(this)
        Log.i("locTag","stopped updating")
    }
    private fun isLocationProviderEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onLocationChanged(location: Location) {
        // Handle location change
        val latitude = location.latitude
        val longitude = location.longitude
        MainActivity.lat =latitude;
        MainActivity.lon = longitude
        SosFragment.latitude = latitude
        SosFragment.longitude = longitude
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
