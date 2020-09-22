package com.sabo.dominik.ontheroadalarm.activities

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.LocationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sabo.dominik.ontheroadalarm.models.Alarm
import com.sabo.dominik.ontheroadalarm.AlarmRepository
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val RESULT_DELETE = -2
    private var finishFlag: Boolean = false
    private var deleteFlag: Boolean = false

    private lateinit var map: GoogleMap
    private val repository: AlarmRepository = AlarmRepository.instance
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var alarmLocation: LatLng
    private var position: Int = 0
    private lateinit var ringtonePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide();

        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
            binding.tvNew.text = repository.alarms[position].name
            binding.etAlarmName.setText(repository.alarms[position].name)
            if(repository.alarms[position].activationDistance >= 1000){
                binding.etDistanceKms.setText((repository.alarms[position].activationDistance/1000).toString())
            }
            else{
                binding.etDistanceKms.setText("0")
            }
            binding.etDistanceMs.setText((repository.alarms[position].activationDistance%1000).toString())
            ringtonePath = Uri.parse(repository.alarms[position].ringtone)
            alarmLocation = repository.alarms[position].location
            binding.btnDelete.visibility = View.VISIBLE;
        }
        else{
            ringtonePath = RingtoneManager.getActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_ALARM
            );
        }


        binding.tvRingtoneName.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)

        setClickListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()


        if(intent.hasExtra("position")){
            map.addCircle(CircleOptions().center(alarmLocation).radius((binding.etDistanceKms.text.toString().toInt()*1000 + binding.etDistanceMs.text.toString().toInt()).toDouble() ).strokeColor(Color.RED))
            map.addMarker(MarkerOptions().position(alarmLocation))
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    alarmLocation,
                    10f
                )
            )
        }

        map.setOnMapClickListener {
            map.clear()
            if(binding.etDistanceKms.text.isNotBlank() && binding.etDistanceMs.text.isNotBlank()){
                map.addCircle(CircleOptions().center(it).radius((binding.etDistanceKms.text.toString().toInt()*1000 + binding.etDistanceMs.text.toString().toInt()).toDouble()).strokeColor(Color.RED))
            }
            else if(binding.etDistanceKms.text.isNotBlank()){
                map.addCircle(CircleOptions().center(it).radius((binding.etDistanceKms.text.toString().toInt()*1000).toDouble()).strokeColor(Color.RED))
            }
            else if(binding.etDistanceMs.text.isNotBlank()){
                map.addCircle(CircleOptions().center(it).radius((binding.etDistanceMs.text.toString().toInt()).toDouble()).strokeColor(Color.RED))
            }
            map.addMarker(MarkerOptions().position(it))
            alarmLocation = it;
        }
    }

    private fun setClickListeners(){
        binding.ivBackArrow.setOnClickListener(){
            finish()
        }

        binding.ivDone.setOnClickListener(){
            if(binding.etDistanceKms.text.isBlank()) binding.etDistanceKms.setText("0")
            if(binding.etDistanceMs.text.isBlank()) binding.etDistanceMs.setText("0")
            if(binding.etDistanceKms.text.toString() == "0" && binding.etDistanceMs.text.toString() == "0"){
                Toast.makeText(this, "Alarm activation distance cannot be 0", Toast.LENGTH_LONG).show()
            }
            else{
                finishFlag = true
                finish()
            }
        }

        binding.btnDelete.setOnClickListener(){
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this alarm?")
                .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                    deleteFlag = true
                    finish()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }

        binding.ivChevron.setOnClickListener(){
            selectRingtone()
        }

        binding.tvRingtoneName.setOnClickListener(){
            selectRingtone()
        }

        binding.btnFavs.setOnClickListener(){

        }
    }

    private fun selectRingtone(){
        if (ContextCompat.checkSelfPermission(
                this@SettingsActivity,
                READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SettingsActivity,
                    READ_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this@SettingsActivity,
                    arrayOf(READ_EXTERNAL_STORAGE),
                    0
                )
            }
        } else {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtonePath)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            startActivityForResult(intent, 999)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 999) {
            val uri = data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                ringtonePath = uri
                binding.tvRingtoneName.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(
                    this
                )
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }




    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            if(!intent.hasExtra("position")) {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val provider: String = locationManager.getBestProvider(Criteria(), true)
                val currentLocation = locationManager.getLastKnownLocation(provider)
                alarmLocation = if(currentLocation == null) LatLng(0.0, 0.0)
                else LatLng(currentLocation.latitude, currentLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(alarmLocation, 10f))
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(ACCESS_FINE_LOCATION),
                1
            )
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {}
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    1
                )
            }
        }
    }

    override fun finish() {
        if(finishFlag){
            if (binding.etAlarmName.text.isBlank()) binding.etAlarmName.setText("Alarm")
            if(intent.hasExtra("position")){
                repository.alarms[position].name = binding.etAlarmName.text.toString()
                repository.alarms[position].location = alarmLocation
                repository.alarms[position].ringtone = ringtonePath.toString()
                repository.alarms[position].activationDistance = (binding.etDistanceKms.text.toString().toInt() * 1000) + binding.etDistanceMs.text.toString().toInt()
                repository.alarms[position].isActive = true
                val data: Intent = Intent()
                data.putExtra("position", position)
                setResult(RESULT_OK, data)
            }
            else{
                repository.add(
                    Alarm(
                        binding.etAlarmName.text.toString(),
                        alarmLocation,
                        ringtonePath.toString(),
                        (binding.etDistanceKms.text.toString().toInt() * 1000) + binding.etDistanceMs.text.toString().toInt(),
                        true
                    )
                )
                setResult(RESULT_OK)
            }
        }
        else if(deleteFlag){
            val data: Intent = Intent()
            data.putExtra("position", position)
            setResult(RESULT_DELETE, data)
            repository.remove(position)
        }
        super.finish()
    }

}