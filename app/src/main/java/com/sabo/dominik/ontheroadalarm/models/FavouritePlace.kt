package com.sabo.dominik.ontheroadalarm.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
class FavouritePlace(var name: String, var latitude: Double, var longitude: Double, var description: String, var picture: String) {

    @PrimaryKey(autoGenerate = true)
    var id:Long? = null

}