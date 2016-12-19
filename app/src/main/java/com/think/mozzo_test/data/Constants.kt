package com.think.mozzo_test.data

import com.google.android.gms.maps.model.LatLng

import java.util.HashMap


object Constants {

    val GEOFENCE_EXPIRATION_IN_MILLISECONDS = (10 * 60 * 60 * 1000).toLong()
    val GEOFENCE_RADIUS_IN_METERS = 100f

    val LANDMARKS = HashMap<String, LatLng>()

    init {


        LANDMARKS.put("Home", LatLng(37.621313, -122.378955))
    }
}
