package com.sabo.dominik.ontheroadalarm.broadcastrecievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.sabo.dominik.ontheroadalarm.activities.AlarmActivity


class GeofenceBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        val position: Int = intent!!.getIntExtra("position", 0)

        val positions = mutableListOf<String>()
        positions.add(position.toString())

        val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context!!.applicationContext)
        geofencingClient.removeGeofences(positions)

        val ringIntent = Intent(context!!.applicationContext, AlarmActivity::class.java)
        ringIntent.putExtra("position", position)
        ringIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            ringIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(ringIntent)

    }
}