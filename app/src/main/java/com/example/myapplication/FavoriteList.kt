package com.example.myapplication

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.models.User
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavoriteList : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var userId: String? = null
    private lateinit var sharedPref: android.content.SharedPreferences
    private lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_list)
        database = Firebase.database.reference
        sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        placesClient = Places.createClient(this)

        val container = findViewById<LinearLayout>(R.id.LikeContainer)
        val userPostsRef = database.child("users").child(userId.toString())
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
//                user?.let {
//                    Log.d("UserInfo", "User details: $it")
//                }
//                val likePlaceInfos = user?.likePlaceInfos
//                val placeIds: List<String?> = likePlaceInfos?.map { it.placeId } ?: emptyList()
//                likePlaceInfos?.forEach { placeInfo ->
//                    Log.d("likePlaceInfos", "Place ID: ${placeInfo.placeId}, Rating: ${placeInfo.rating}, Notes: ${placeInfo.notes}")
//                }
//                placeIds.forEach { placeId ->
//                    Log.d("placeIds", "Place ID: $placeId")
//                }
                val placeIds = user?.likePlaceIds?.toList()
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME)

                if (placeIds != null) {
                    val options = mutableListOf<String>()
                    var counter = 0

                    for (placeId in placeIds) {
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response ->
                                Log.i("list", "Place: ${response.place.name}")
                                // 動態新增貼文
                                val cardView = CardView(this@FavoriteList)
                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    500
                                )
                                layoutParams.setMargins(16, 16, 16, 16)


                                cardView.layoutParams = layoutParams
                                cardView.cardElevation = 5f
                                val editTextContainer = LinearLayout(this@FavoriteList)
                                editTextContainer.orientation = LinearLayout.VERTICAL
                                // 店名
                                val textView = TextView(this@FavoriteList)
                                val text = "${response.place.name}"
                                textView.text = text
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                                textView.setPadding(16, 16, 16, 16)
                                textView.setTypeface(Typeface.DEFAULT_BOLD)
                                // 将TextView添加到CardView中
                                editTextContainer.addView(textView)

                                val editText1 = EditText(this@FavoriteList)
                                editText1.hint = "請輸入評分"
                                editText1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText1.setPadding(16, 0, 16, 16)

                                editText1.setBackgroundResource(R.drawable.edittext2)

                                val layoutparams1 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                layoutparams1.weight = 1f
                                layoutparams1.height = 20
                                layoutparams1.width = 225
                                editText1.layoutParams = layoutparams1
                                editTextContainer.addView(editText1)

                                val editText2 = EditText(this@FavoriteList)
                                editText2.hint = "請輸入評價"
                                editText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText2.setPadding(16, 0, 16, 16)

                                editText2.setBackgroundResource(R.drawable.edittext2)

                                val layoutparams2 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                layoutparams2.weight = 1f
                                layoutparams2.height = 90
                                editText2.layoutParams = layoutparams2
                                editText2.setLines(5)

                                editTextContainer.addView(editText2)

                                val editText3 = EditText(this@FavoriteList)
                                editText3.hint = "備註欄"
                                editText3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText3.setPadding(16, 0, 16, 16)

                                editText3.setBackgroundResource(R.drawable.edittext2)

                                val layoutparams3 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                layoutparams3.weight = 1f
                                layoutparams3.height = 90
                                editText3.layoutParams = layoutparams3
                                editText3.setLines(5)

                                editTextContainer.addView(editText3)

                                cardView.addView(editTextContainer)
                                container.addView(cardView)
                            }
                    }

                }
                // 处理帖子数量，例如更新UI
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })
    }
}