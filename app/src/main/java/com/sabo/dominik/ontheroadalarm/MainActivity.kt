package com.sabo.dominik.ontheroadalarm

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.sabo.dominik.ontheroadalarm.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), AlarmClickInterface {

    private val NEW_REQUEST_CODE = 1
    private val EDIT_REQUEST_CODE = 2
    private val RESULT_DELETE = -2

    private val repository: AlarmRepository = AlarmRepository.instance

    private lateinit var geofencingClient : GeofencingClient
    private lateinit var adapter: RecyclerAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(repository.alarms.isEmpty()) repository.loadData(this)

        geofencingClient = LocationServices.getGeofencingClient(this)

        adapter = RecyclerAdapter(this)

        binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rvRecyclerView.adapter = adapter

        adapter.addData(repository.alarms)

        binding.btnAdd.setOnClickListener(){
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivityForResult(intent, NEW_REQUEST_CODE)
        }
    }

    override fun onStop() {
        repository.saveData(this)
        super.onStop()
    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode){
            RESULT_OK -> {
                when (requestCode) {
                    NEW_REQUEST_CODE -> {
                        adapter.addItem(repository.alarms.last())
                        setGeoalarm(repository.alarms.size - 1)
                        Toast.makeText(applicationContext, "Geoalarm set", Toast.LENGTH_SHORT).show()
                    }
                    EDIT_REQUEST_CODE -> {
                        val position: Int = data!!.getIntExtra("position", 0)
                        adapter.changeItem(repository.alarms[position], position)
                        setGeoalarm(position)
                        Toast.makeText(applicationContext, "Geoalarm set", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            RESULT_DELETE -> {
                val position = data!!.getIntExtra("position", 0)
                adapter.removeItem(position)
                geofencingClient.removeGeofences(getPendingIntent(position))

                for (i in position until repository.alarms.size) {
                    unsetGeoalarm(i)
                }

                for (i in position until repository.alarms.size) {
                    if (repository.alarms[position].isActive) {
                        setGeoalarm(i)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAlarmClick(position: Int) {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        intent.putExtra("position", position)
        startActivityForResult(intent, EDIT_REQUEST_CODE)
    }

    override fun onSwitchClick(position: Int) {
        Toast.makeText(this, position.toString(), Toast.LENGTH_SHORT).show()
        repository.alarms[position].toggle()
        if(repository.alarms[position].isActive){
            setGeoalarm(position)
            Toast.makeText(applicationContext, "Geoalarm set", Toast.LENGTH_SHORT).show()
        }
        else{
            unsetGeoalarm(position)
            Toast.makeText(applicationContext, "Geoalarm unset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun unsetGeoalarm(position: Int) {
        geofencingClient.removeGeofences(getPendingIntent(position))
    }

    @SuppressLint("MissingPermission")
    private fun setGeoalarm(position: Int) {
        val geofencingRequest: GeofencingRequest = getGeofencingRequest(position)
        val pendingIntent: PendingIntent = getPendingIntent(position)
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
    }

    private fun getPendingIntent(position: Int): PendingIntent {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.putExtra("position", position)
        return PendingIntent.getBroadcast(this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofencingRequest(position: Int): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(buildGeofence(position))
        }.build()
    }


    private fun buildGeofence(position: Int):Geofence{
        return Geofence.Builder()
            .setRequestId(position.toString())
            .setCircularRegion(
                repository.alarms[position].location.latitude,
                repository.alarms[position].location.longitude,
                repository.alarms[position].activationDistance.toFloat() * 1000
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
    }





}