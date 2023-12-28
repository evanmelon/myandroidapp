package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.User
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Newpost : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var readWriteSnippets: ReadAndWriteSnippets
    private lateinit var Title : EditText
    private lateinit var Content: EditText
    private lateinit var addpostButton: Button
    private lateinit var addrestaurant: Button
    private lateinit var placesClient: PlacesClient
    private lateinit var sharedPref: android.content.SharedPreferences
    private lateinit var restaurantContent: TextView
    private lateinit var placeId: String
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpost)
        database = Firebase.database.reference
        sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        byId()
        sendpost()
        readWriteSnippets = ReadAndWriteSnippets()
        readWriteSnippets.initializeDbRef()
        placesClient = Places.createClient(this)
        restaurantContent = findViewById(R.id.restaurantContent)
        addrestaurant = findViewById(R.id.addRestaurant)
        addrestaurant.setOnClickListener {
            val userPostsRef = database.child("users").child(userId.toString())
            userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
//                    val options = user?.likePlaceIds?.toTypedArray()
                    val likePlaceInfos = user?.likePlaceInfos?.toList()
                    val placeIds: List<String?> = likePlaceInfos?.map { it.placeId } ?: emptyList()
//                    val placeIds = user?.likePlaceIds?.toList()
                    val placeFields = listOf(Place.Field.ID, Place.Field.NAME)
                    val options = mutableListOf<String>()
                    if (placeIds != null) {
                        val options = mutableListOf<String>()
                        var counter = 0

                        for (placeId in placeIds) {
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    options.add(response.place.name)
                                    counter++
                                    if (counter == placeIds.size) {
                                        showAlertDialog(options, placeIds, placeFields)
                                    }
                                }
                                .addOnFailureListener {
                                    // 处理错误
                                    counter++
                                    if (counter == placeIds.size) {
                                        showAlertDialog(options, placeIds, placeFields)
                                    }
                                }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // 处理错误
                }
            })
        }
    }
    fun showAlertDialog(options: List<String>, placeIds: List<String?>, placeFields: List<Place.Field>) {
        val builder = AlertDialog.Builder(this@Newpost)
        builder.setTitle("Select a place")
            .setItems(options.toTypedArray()) { _, which ->
                // 对话框的点击事件处理
                placeId = placeIds[which].toString()
                val request = FetchPlaceRequest.newInstance(placeIds[which], placeFields)
                placesClient.fetchPlace(request)
                    .addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place
                        Log.i("place", "Place select: ${place.name}")
                        // 设置 TextView 的文本并使其可见
                        restaurantContent.text = place.name
                        restaurantContent.visibility = View.VISIBLE
                    }
                    .addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e("place", "Place not found: ${exception.message}")
                            // 处理错误
                        }
                    }
            }
        val dialog = builder.create()
        dialog.show()
    }
    private fun byId() {
        Title = findViewById(R.id.Title)
        Content = findViewById(R.id.Content)
    }

    private fun sendpost() {
        addpostButton = findViewById(R.id.addpost)
        addpostButton.setOnClickListener {
            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("USER_ID", null)
            val name = sharedPref.getString("NAME", null)
            val email = sharedPref.getString("EMAIL", null)
            val Tstr = Title.text.toString()    // 拿取 Title 字串
            val Cstr = Content.text.toString()  // 拿取 Content 字串
            // 更新資料庫
            readWriteSnippets.writeNewPost(userId = userId.toString(), username = name.toString(), title = Tstr, body = Cstr, placeID = placeId)
//            data.userdata?.writeNewPost(userId = userId.toString(), username = name.toString(), title = Tstr, body = Cstr)
//            Post(userId.toString(), name.toString(), Tstr, Cstr)
            // 回到個人頁面
            val intent = Intent(this, Personal::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}
