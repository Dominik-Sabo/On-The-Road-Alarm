package com.sabo.dominik.ontheroadalarm.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
class Alarm(var name: String, var latitude: Double, var longitude: Double, var ringtone: String, var activationDistance: Int, var isActive: Boolean) {

    @PrimaryKey(autoGenerate = true)
    var id:Long? = null

    fun toggle(){
        isActive = !isActive
    }
}