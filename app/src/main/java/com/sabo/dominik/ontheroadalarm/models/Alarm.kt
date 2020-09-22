package com.sabo.dominik.ontheroadalarm.models

import com.google.android.gms.maps.model.LatLng

class Alarm(var name: String, var location: LatLng, var ringtone: String, var activationDistance: Int, var isActive: Boolean) {

    fun toggle(){
        isActive = !isActive
    }
}