package com.example.wanderwings;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.MainThread;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocation {
    static Geocoder geocoder;
    static String addressString = "";
    public static String getLocation(double latitude, double longitude,Context c){
        addressString = "";
        geocoder = new Geocoder(c, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            // Get the first address from the list
            Address address = addresses.get(0);

            // Get the address lines as a string

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressString += address.getAddressLine(i) + "\n";
            }

        }
        return addressString;
    }
}
