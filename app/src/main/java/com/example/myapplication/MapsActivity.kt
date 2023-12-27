package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.BooleanPlaceAttributeValue
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.net.URL

/**
 * An activity that displays a map showing the place at the device's current location.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private var map: GoogleMap? = null
    private lateinit var database: DatabaseReference

    private var cameraPosition: CameraPosition? = null
    private lateinit var sharedPref: android.content.SharedPreferences
    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
    private val delayMillis = 5000L // 5 seconds delay
    private val mapUpdateDelayMillis = 500L // 5 seconds delay
    private var zoomLevel: Float = 15f
    var userLatLng: LatLng = LatLng(121.0, 25.0)
    private var userId: String? = null
    // [START maps_current_place_on_create]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        // [START_EXCLUDE silent]
        // Retrieve location and camera position from saved instance state.
        // [START maps_current_place_on_create_save_instance_state]
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(applicationContext, "AIzaSyDGLzfM68E4GuGew5K2MXu04CPXa3afaUI")
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        // [START maps_current_place_map_fragment]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]
        val homeButton: Button = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {

            val intent = Intent(this, Home::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // 用户已登录
                Log.d("login", "user login")
                val intent = Intent(this, Personal::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

            }
            else {
                // 用户未登录
                Log.d("login", "user not login")
                val intent = Intent(this, FirebaseUIActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }

//            if (sharedPref != null) {
//                val intent = Intent(this, Personal::class.java)
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//            } else {
//                // key不存在，执行相应的逻辑
//                val intent = Intent(this, FirebaseUIActivity::class.java)
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
//            }

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        setupEditTextListener()

        val spinButton: ImageView = findViewById(R.id.myImageButton)
        spinButton.setOnClickListener {
            val intent = Intent(this, Spin::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

    // [END maps_current_place_on_create]

    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.current_place_menu, menu)
//        return true
//    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    // [START maps_current_place_on_options_item_selected]
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.option_get_place) {
//            showCurrentPlace()
//        }
//        return true
//    }
    // [END maps_current_place_on_options_item_selected]

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // [START maps_current_place_on_map_ready]
    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map?.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false)
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
        // 初始化 LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    lastKnownLocation = location
                    Log.d("location update", "hi")
//                    updateLocationUI()
//                    getDeviceLocation()
                }
            }
        }
        this.map?.setOnInfoWindowClickListener(this)
    }
    // [END maps_current_place_on_map_ready]

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
//                        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
                        map?.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }
    // [END maps_current_place_on_request_permissions_result]

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    // [START maps_current_place_show_current_place]
    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count = if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                        likelyPlaces.placeLikelihoods.size
                    } else {
                        M_MAX_ENTRIES
                    }
                    var i = 0
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog()
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            map?.addMarker(MarkerOptions()
                .title(getString(R.string.default_info_title))
                .position(defaultLocation)
                .snippet(getString(R.string.default_info_snippet)))

            // Prompt the user for permission.
            getLocationPermission()
        }
    }
    // [END maps_current_place_show_current_place]

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    // [START maps_current_place_open_places_dialog]
    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { dialog, which -> // The "which" argument contains the position of the selected item.
            val markerLatLng = likelyPlaceLatLngs[which]
            var markerSnippet = likelyPlaceAddresses[which]
            if (likelyPlaceAttributions[which] != null) {
                markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
            }

            if (markerLatLng == null) {
                return@OnClickListener
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            map?.addMarker(MarkerOptions()
                .title(likelyPlaceNames[which])
                .position(markerLatLng)
                .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()
    }
    // [END maps_current_place_open_places_dialog]
    // 隐藏软键盘的方法
    private fun hideKeyboard() {
        val editText: EditText = findViewById(R.id.editText)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
    private fun setupEditTextListener() {
        val editText = findViewById<EditText>(R.id.editText)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleSearchAction(editText.text.toString())
                true // 表示事件已被处理
            } else {
                false
            }
        }
    }
    // 处理搜索动作的方法
    private fun handleSearchAction(query: String) {
        hideKeyboard()
        Log.d("hideKeyboard", "Keyboard hidden")

        // 确保已经获取到了位置权限
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限...
            return
        }

        Log.d("permission", "Got permission")

        // 执行搜索和更新地图的操作
        performSearch(query)
    }
    // 执行搜索的方法
    private fun performSearch(query: String) {
        // Specify the list of fields to return.
        map?.clear()
        // Specify the list of fields to return.
        val placeFields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ICON_URL,
            Place.Field.ADDRESS,
            Place.Field.CURRENT_OPENING_HOURS,
            Place.Field.PRICE_LEVEL,
            Place.Field.SERVES_VEGETARIAN_FOOD,
            Place.Field.EDITORIAL_SUMMARY,
            Place.Field.RATING ,
            Place.Field.PHONE_NUMBER
        )
        // Define latitude and longitude coordinates of the search area.

        // Define latitude and longitude coordinates of the search area.
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                    }
                    else{
                        Log.d("lastKnownLocation", "failed")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
        // 假设已经获取到了当前位置的经纬度
        lastKnownLocation?.let { location ->
            val currentLatitude = location.latitude
            val currentLongitude = location.longitude

            // 使用当前位置坐标创建矩形搜索区域

            // 使用当前位置坐标创建矩形搜索区域
            val southWest = LatLng(currentLatitude - 0.1, currentLongitude - 0.1)
            val northEast = LatLng(currentLatitude + 0.1, currentLongitude + 0.1)

            Log.d("query", query)
            // Use the builder to create a SearchByTextRequest object.
            val searchByTextRequest: SearchByTextRequest =
                SearchByTextRequest.builder(query, placeFields)
                    .setMaxResultCount(10)
                    .setLocationRestriction(
                        RectangularBounds.newInstance(
                            southWest,
                            northEast
                        )
                    ).build()

            // Call PlacesClient.searchByText() to perform the search.
            // Define a response handler to process the returned List of Place objects.
            placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener { response ->
                    val places: List<Place> = response.places
                    Log.d("places", "place ${places.size}")
                    showOptionsDialog(places)
                    for (place in places) {
                        val placeId = place.id
                        val placeName = place.name

                        val placeAddress = place.address
                        val placeRating = place.rating

                        val spotLatLng = place.latLng
                        val current_opening_h = place.currentOpeningHours
                        val price_level = place.priceLevel
                        val serves_veg = place.servesVegetarianFood
                        val phone_number = place.phoneNumber

                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val url = URL(place.iconUrl)
                                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                                withContext(Dispatchers.Main) {
                                    val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
                                    val markerData = MarkerData(
                                        id = place.id,
                                        name = place.name,
                                        address = place.address,
                                        current_opening_hours = place.currentOpeningHours,
                                        serves_vegentarian_food = place.servesVegetarianFood,
                                        editorial_summary = place.editorialSummary,
                                        ratting = place.rating,
                                        price_level = place.priceLevel,
                                        phone_number = place.phoneNumber
                                        )
                                    val spot = map?.addMarker(
                                        MarkerOptions()
                                            .position(spotLatLng)
                                            .title(placeName)
                                            .icon(icon)
                                            .snippet(place.editorialSummary)
                                    )
                                    spot?.tag = markerData
                                    spot?.showInfoWindow()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // 处理异常
                            }
                        }


                        // 其他信息...
                        Log.d(
                            "places",
                            "placeId: $placeId, placeName: $placeName, placeAddress: $placeAddress, placeRating: $placeRating"
                        )
                        // 你可以在这里执行你的操作，比如显示在 UI 中
                        // 添加标记到地图上

                    }
                }
        }
    }
    fun showOptionsDialog(places: List<Place>) {
        val options = places.map { it.name }.toTypedArray()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a place")
            .setItems(options) { _, which ->
                handleSelectedPlace(places[which])
            }

        val dialog = builder.create()
        dialog.show()
    }
    fun handleSelectedPlace(selectedPlace: Place) {
        val dialogView = layoutInflater.inflate(R.layout.button_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        val tvStoreName: TextView = dialogView.findViewById(R.id.tvStoreName)
        val tvStoreAddr: TextView = dialogView.findViewById(R.id.tvStoreAddr)
        val tvPhone: TextView = dialogView.findViewById(R.id.tvPhone)
        val tvRating: TextView = dialogView.findViewById(R.id.tvRating)
        val tvPrice: TextView = dialogView.findViewById(R.id.tvPrice)
        val tvDescription: TextView = dialogView.findViewById(R.id.tvDescription)
        val likebutton: Button = dialogView.findViewById(R.id.like)
        val markerData = MarkerData(
            id = selectedPlace.id,
            name = selectedPlace.name,
            address = selectedPlace.address,
            current_opening_hours = selectedPlace.currentOpeningHours,
            serves_vegentarian_food = selectedPlace.servesVegetarianFood,
            editorial_summary = selectedPlace.editorialSummary,
            ratting = selectedPlace.rating,
            price_level = selectedPlace.priceLevel,
            phone_number = selectedPlace.phoneNumber
        )

        // 现在可以使用 markerData 中的信息了
        tvStoreName.text = markerData?.name
        tvStoreAddr.text = markerData?.address
        tvPhone.text = markerData?.phone_number
        tvRating.text = markerData?.ratting.toString()
        tvPrice.text = markerData?.price_level.toString()
        tvDescription.text = markerData?.editorial_summary


        dialog.show()

        likebutton.setOnClickListener {
            Log.d("like", " ${markerData.name}")
            sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            userId = sharedPref.getString("USER_ID", null)
            val userRef = database.child("users").child(userId.toString())
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        // 更新 placeIds 列表
                        val updatedPlaceIds = it.likePlaceIds?.toMutableList() ?: mutableListOf()
                        updatedPlaceIds.add(markerData.id.toString())

                        // 将更新后的对象写回数据库
                        userRef.updateChildren(mapOf("likePlaceIds" to updatedPlaceIds))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 处理错误
                }
            })
            Toast.makeText(
                this, "${markerData.name} 加入Likes",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    class MarkerData(
        val id: String?,
        val name: String?,
        val address: String?,
        val current_opening_hours: OpeningHours?,
        val serves_vegentarian_food: BooleanPlaceAttributeValue?,
        val editorial_summary: String?,
        val ratting: Double?,
        val price_level: Int?,
        val phone_number: String?
        )
    override fun onInfoWindowClick(marker: Marker) {
        val dialogView = layoutInflater.inflate(R.layout.button_sheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        val tvStoreName: TextView = dialogView.findViewById(R.id.tvStoreName)
        val tvStoreAddr: TextView = dialogView.findViewById(R.id.tvStoreAddr)
        val tvPhone: TextView = dialogView.findViewById(R.id.tvPhone)
        val tvRating: TextView = dialogView.findViewById(R.id.tvRating)
        val tvPrice: TextView = dialogView.findViewById(R.id.tvPrice)
        val tvDescription: TextView = dialogView.findViewById(R.id.tvDescription)
        val markerData = marker.tag as? MarkerData

        // 现在可以使用 markerData 中的信息了
        tvStoreName.text = markerData?.name
        tvStoreAddr.text = markerData?.address
        tvPhone.text = markerData?.phone_number
        tvRating.text = markerData?.ratting.toString()
        tvPrice.text = markerData?.price_level.toString()
        tvDescription.text = markerData?.editorial_summary


        dialog.show()
        Toast.makeText(
            this, "${marker.snippet}",
            Toast.LENGTH_SHORT
        ).show()
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }
}
