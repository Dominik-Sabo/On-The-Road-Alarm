package com.sabo.dominik.ontheroadalarm.activities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
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
import com.sabo.dominik.ontheroadalarm.FavouriteRepository
import com.sabo.dominik.ontheroadalarm.R
import com.sabo.dominik.ontheroadalarm.databinding.ActivityEditFavouriteBinding
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace
import java.io.FileNotFoundException
import java.io.InputStream

class EditFavouriteActivity : AppCompatActivity(), OnMapReadyCallback {

    private val GALLERY_REQUEST = 999
    private val CAMERA_REQUEST = 1888
    private val RESULT_DELETE = -2

    private var hasPicture: Boolean = false
    private lateinit var bitmap: Bitmap

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

        supportActionBar?.hide();

        if(intent.hasExtra("position")){
            position = intent.getIntExtra("position", 0)
            binding.tvNew.text = repository.favourites[position].name
            binding.etFavName.setText(repository.favourites[position].name)
            binding.etFavDescription.setText(repository.favourites[position].description)
            bitmap = repository.favourites[position].picture
            binding.ivImg.setImageBitmap(bitmap)
            favLocation = repository.favourites[position].location
            binding.btnDelete.visibility = View.VISIBLE;
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
            favLocation = it;
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
                val provider: String = locationManager.getBestProvider(Criteria(), true)
                val currentLocation = locationManager.getLastKnownLocation(provider)
                favLocation = if(currentLocation == null) LatLng(0.0, 0.0)
                else LatLng(currentLocation.latitude, currentLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(favLocation, 10f))
                map.addMarker(MarkerOptions().position(favLocation))
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
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

    private fun setClickListeners(){
        binding.ivBackArrow.setOnClickListener(){
            finish()
        }

        binding.ivDone.setOnClickListener(){
            finishFlag = true
            finish()
        }

        binding.btnDelete.setOnClickListener(){
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this favourite place?")
                .setPositiveButton(android.R.string.yes) { _: DialogInterface, _: Int ->
                    deleteFlag = true
                    finish()
                }
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
        
        binding.btnSelectImg.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST)
        }

        binding.btnCamera.setOnClickListener(){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.CAMERA),
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
                        val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        bitmap = selectedImage
                        hasPicture = true
                        binding.ivImg.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                CAMERA_REQUEST -> {
                    bitmap = data!!.extras!!["data"] as Bitmap
                    binding.ivImg.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun finish() {
        if(finishFlag){
            if(intent.hasExtra("position")) {
                repository.favourites[position].name = binding.etFavName.text.toString()
                repository.favourites[position].location = favLocation
                repository.favourites[position].description = binding.etFavDescription.text.toString()
                repository.favourites[position].picture = bitmap
                val data: Intent = Intent()
                data.putExtra("position", position)
                setResult(RESULT_OK, data)
            }
            else{
                repository.add(
                    FavouritePlace(
                        binding.etFavName.text.toString(),
                        favLocation,
                        binding.etFavDescription.text.toString(),
                        bitmap
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