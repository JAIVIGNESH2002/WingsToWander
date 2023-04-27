package com.example.wanderwings

import android.telephony.SmsManager


class SosMessage {
    val latitude = SosFragment.latitude

    companion object{
        fun sendSos(){
            val smsManager = SmsManager.getDefault()
            val message = "I am in danger my current location is : https://www.google.com/maps?q=${MainActivity.lat},${MainActivity.lon}"
            val phoneNumber = "+919952522935" // Replace with desired phone number
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
        fun sosLight(){

        }

    }

}