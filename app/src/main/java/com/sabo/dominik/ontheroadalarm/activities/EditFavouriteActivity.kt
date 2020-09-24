package com.sabo.dominik.ontheroadalarm.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Criteria
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sabo.dominik.ontheroadalarm.repository.FavouriteRepository
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.databinding.ActivityEditFavouriteBinding
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace
import java.io.*
import java.io.File.separator

class EditFavouriteActivity : AppCompatActivity(), OnMapReadyCallback {

    private val GALLERY_REQUEST = 999
    private val CAMERA_REQUEST = 1888
    private val RESULT_DELETE = -2

    private var hasPicture: Boolean = false
    private lateinit var picture: Uri

    private var finishFlag: Boolean = false
    private var deleteFlag: Boolean = false

    private lateinit var map: GoogleMap

    private val repository: FavouriteRepository = FavouriteRepository.instance
    private lateinit var binding: ActivityEditFavouriteBinding
    private lateinit var favLocation: LatLng
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_favourite)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.hide()

        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
            binding.tvNew.text = repository.favourites[position].name
            binding.etFavName.setText(repository.favourites[position].name)
            binding.etFavDescription.setText(repository.favourites[position].description)
            picture = Uri.parse(repository.favourites[position].picture)
            binding.ivImg.setImageURI(picture)
            favLocation = LatLng(repository.favourites[position].latitude, repository.favourites[position].longitude)
            binding.btnDelete.visibility = View.VISIBLE
        }

        setClickListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()

        if(intent.hasExtra("position")){
            map.addMarker(MarkerOptions().position(favLocation))
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    favLocation,
                    10f
                )
            )
        }

        map.setOnMapClickListener {
            map.clear()
            map.addMarker(MarkerOptions().position(it))
            favLocation = it
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            if(!intent.hasExtra("position")) {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val provider = locationManager.getBestProvider(Criteria(), true)
                val currentLocation = locationManager.getLastKnownLocation(provider!!)
                favLocation = if(currentLocation == null) LatLng(0.0, 0.0)
                else LatLng(currentLocation.latitude, currentLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(favLocation, 10f))
                map.addMarker(MarkerOptions().position(favLocation))
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    1
                )
            }
        }
    }

    private fun setClickListeners(){
        binding.ivBackArrow.setOnClickListener {
            finish()
        }

        binding.ivDone.setOnClickListener {
            if(this::picture.isInitialized){
                finishFlag = true
                finish()
            }
            else Toast.makeText(this, "Select an image!", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this favourite place?")
                .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                    deleteFlag = true
                    finish()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        
        binding.btnSelectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST)
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(reqCode){
                GALLERY_REQUEST -> {
                    try {
                        val imageUri: Uri = data?.data!!
                        picture = imageUri
                        hasPicture = true
                        binding.ivImg.setImageURI(picture)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                CAMERA_REQUEST -> {
                    val bitmap = data!!.extras!!["data"] as Bitmap
                    saveImage(bitmap, this, "OnTheRoadAlarm")
                    binding.ivImg.setImageURI(picture)
                }
            }
        }
    }

    override fun finish() {
        if(finishFlag){
            if(binding.etFavName.text.isBlank()) binding.etFavName.setText("Favourite")
            val favPlace = FavouritePlace(
                binding.etFavName.text.toString(),
                favLocation.latitude,
                favLocation.longitude,
                binding.etFavDescription.text.toString(),
                picture.toString()
            )
            if(intent.hasExtra("position")) {
                repository.update(position, favPlace, application)
                val data = Intent()
                data.putExtra("position", position)
                setResult(RESULT_OK, data)
            }
            else{
                repository.add(favPlace, application)
                setResult(RESULT_OK)
            }
        }
        else if(deleteFlag){
            val data = Intent()
            data.putExtra("position", position)
            setResult(RESULT_DELETE, data)
            repository.remove(position, application)
        }
        super.finish()
    }

    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                picture = Uri.fromFile(file)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put("datetaken", System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}