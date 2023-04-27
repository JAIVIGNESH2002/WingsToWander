package com.example.wanderwings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var smsManager: SmsManager
    private lateinit var rippleLottie:LottieAnimationView
    companion object{
         var latitude:Double = 0.0
         var longitude:Double = 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        smsManager = SmsManager.getDefault()
        rippleLottie = view.findViewById<LottieAnimationView>(R.id.rippleLottie)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context,"Permission denied",Toast.LENGTH_SHORT).show()
            return
        }
        val addressTv = view.findViewById<TextView>(R.id.addressTv)
        val sosImage = view.findViewById<ImageView>(R.id.sosImg)
        sosImage.setOnClickListener {
            sendLocationLink(latitude, longitude)
        }
        Toast.makeText(context,GetLocation.getLocation(latitude,longitude,context),Toast.LENGTH_SHORT).show()
        addressTv.setText(GetLocation.getLocation(latitude,longitude,context))
    }
    private fun sendLocationLink(latitude:Double,longitude:Double) {
        val message = "I am in danger my current location is : https://www.google.com/maps?q=$latitude,$longitude"
        val phoneNumber = "+919952522935" // Replace with desired phone number
        Toast.makeText(context,"SOS send successfully !",Toast.LENGTH_SHORT).show()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }
}