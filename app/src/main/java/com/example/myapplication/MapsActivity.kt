package com.example.myapplication

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMapsBinding
import com.example.myapplication.ui.theme.Spin
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 5000L // 5 seconds delay
    private val mapUpdateDelayMillis = 500L // 5 seconds delay
    private var zoomLevel: Float = 15f

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            // Set an exit transition
            enterTransition = Fade()
            exitTransition = Fade()

        }
        supportActionBar?.hide()
        val message = "Hello, Android!"
        Log.d("MyTag", message)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val myButton: Button = findViewById(R.id.myButton)

        myButton.setOnClickListener {
            // 在这里处理按钮点击事件，例如导航到登入页面
            val intent = Intent(this, FirebaseUIActivity::class.java)
            startActivity(intent)
        }
        val homeButton: Button = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {

            val intent = Intent(this, Home::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        val editText = findViewById<EditText>(R.id.editText)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Handle Enter key press
                val location = editText.text.toString()
                // Perform action with the entered location, e.g., search on the map
                true
            } else {
                false
            }
        }
        val spinButton: ImageView = findViewById(R.id.spinbutton)
        spinButton.setOnClickListener {
            val intent = Intent(this, Spin::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // 初始化 FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 初始化 LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocationOnMap(location)
                }
            }
        }
        // 请求定位权限
        requestLocationPermission()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        var isIdleUpdate: Boolean = false
        val myButton: Button = findViewById(R.id.myButton)
        val editText: EditText = findViewById(R.id.editText)
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.0, 121.0), zoomLevel))
        mMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                Log.d("location", "CameraMove Started")
                // User started moving the map, stop location updates for a while
                myButton.visibility = View.INVISIBLE
                editText.visibility = View.INVISIBLE
                stopLocationUpdates()
                isIdleUpdate = false
            }
        }
        mMap.setOnCameraMoveListener {
            // Handle camera move event
            // You can check if the map is actively moving, and decide whether to start or stop location updates
            Log.d("location", "Map is moving")
//            stopLocationUpdates()
        }
        mMap.setOnCameraIdleListener {
            Log.d("location", "Camera Idle")
            // User stopped moving the map, start or restart location updates
            zoomLevel = mMap.cameraPosition.zoom
            Log.d("MapZoom", "Zoom Level: $zoomLevel")
            if (!isIdleUpdate){
                myButton.visibility = View.VISIBLE
                editText.visibility = View.VISIBLE
                startLocationUpdates()
                isIdleUpdate = true
            }

        }
        mMap.setOnCameraMoveCanceledListener {
            Log.d("location", "Camera Move Canceled")
            startLocationUpdates()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 已经授予定位权限，可以获取用户位置
            // 调用获取位置的方法
            // ...
            // 在Activity或Fragment中
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // 在这里处理获取到的用户位置
                    if (location != null) {
                        Log.e("Location","${location.latitude}, ${location.longitude}")
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                }
                .addOnFailureListener { e ->
                    // 处理位置获取失败的情况
                    Log.e("Location", "Error getting location: ${e.message}")
                }
        } else {
            // 请求定位权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
        startLocationUpdates()


//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(25.0330, 121.5654)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13f))
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(mapUpdateDelayMillis)  // 更新间隔为 0.5 秒
        Log.d("location", "start Location update")
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("location", "permission checked")
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun updateLocationOnMap(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        val blueMarkerIcon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_myplaces)
        mMap.clear()

        mMap.addMarker(
            MarkerOptions()
                .position(userLatLng)
                .title("Your Location")
                .icon(blueMarkerIcon)
                .anchor(0.5f, 1.0f)
        )
//        mMap.animateCamera(CameraUpdateFactory.newLatLng(userLatLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
    }
    private fun stopLocationUpdatesForAWhile() {
        // Stop location updates for a while
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Post a delayed task to resume location updates after a delay
        handler.postDelayed({
            startLocationUpdates()
        }, delayMillis)
    }
    private fun stopLocationUpdates() {
        val myRunnable = Runnable {
            startLocationUpdates()
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handler.removeCallbacks(myRunnable)
    }
}