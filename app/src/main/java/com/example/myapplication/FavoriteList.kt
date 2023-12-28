package com.example.myapplication

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.models.PlaceInfo
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
        supportActionBar?.title = "Favorite List"
        database = Firebase.database.reference
        sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        placesClient = Places.createClient(this)
        val container = findViewById<LinearLayout>(R.id.LikeContainer)
        val userPostsRef = database.child("users").child(userId.toString())
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Log.d("UserInfo", "User3")
//                user?.let {
//                    Log.d("UserInfo", "User details: $it")
//                }
                val likePlaceInfos = user?.likePlaceInfos
                val placeIds: List<String?> = likePlaceInfos?.map { it.placeId } ?: emptyList()
//                likePlaceInfos?.forEach { placeInfo ->
//                    Log.d("likePlaceInfos", "Place ID: ${placeInfo.placeId}, Rating: ${placeInfo.rating}, Notes: ${placeInfo.notes}")
//                }
//                placeIds.forEach { placeId ->
//                    Log.d("placeIds", "Place ID: $placeId")
//                }
//                val placeIds = user?.likePlaceIds?.toList()
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME)

                if (placeIds != null) {
                    val options = mutableListOf<String>()
                    var counter = 0

                    for (placeId in placeIds) {
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response ->
                                Log.i("list", "Place: ${response.place.name}")
                                val updatedLikePlaceInfos =
                                    user?.likePlaceInfos?.toMutableList()
                                        ?: mutableListOf()
                                val indexToUpdate =
                                    updatedLikePlaceInfos.indexOfFirst { placeInfo ->
                                        placeInfo.placeId == placeId // 用特定的条件定位到要更新的对象
                                    }
                                // 動態新增貼文
                                val cardView = CardView(this@FavoriteList)
                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    850
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

                                val horizontalLayout = LinearLayout(this@FavoriteList)
                                horizontalLayout.orientation = LinearLayout.HORIZONTAL
                                val horizontalLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                horizontalLayout.layoutParams = horizontalLayoutParams
                                val descriptionTextView = TextView(this@FavoriteList)
                                descriptionTextView.text = "評分" // 例如，对于评分的 EditText
                                descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                descriptionTextView.setPadding(16, 8, 16, 8)

                                val descriptionLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                descriptionLayoutParams.height = 100
                                descriptionTextView.layoutParams = descriptionLayoutParams
                                horizontalLayout.addView(descriptionTextView)

                                val editText1 = EditText(this@FavoriteList)
//                                editText1.hint = updatedLikePlaceInfos[indexToUpdate].rating?.toInt().toString()
                                editText1.setText(updatedLikePlaceInfos[indexToUpdate].rating?.toInt().toString())
                                editText1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText1.setPadding(16, 8, 16, 8)

                                editText1.setBackgroundResource(R.drawable.edittext2)
                                var marginInDp = 5
                                var scale = resources.displayMetrics.density
                                var marginInPx = (marginInDp * scale + 0.5f).toInt() // 将 dp 转换为像素
                                val layoutparams1 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
//                                layoutparams1.weight = 1f
//                                layoutparams1.height = 10
                                layoutparams1.width = 225


                                layoutparams1.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                                editText1.layoutParams = layoutparams1
                                horizontalLayout.addView(editText1)
                                editTextContainer.addView(horizontalLayout)


                                //評價
                                val horizontalLayout2 = LinearLayout(this@FavoriteList)
                                horizontalLayout2.orientation = LinearLayout.HORIZONTAL
                                val horizontalLayoutParams2 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                horizontalLayout2.layoutParams = horizontalLayoutParams2
                                val descriptionTextView2 = TextView(this@FavoriteList)
                                descriptionTextView2.text = "評價" // 例如，对于评分的 EditText
                                descriptionTextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                descriptionTextView2.setPadding(16, 8, 16, 8)

                                val descriptionLayoutParams2 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                descriptionLayoutParams2.height = 100
                                descriptionTextView2.layoutParams = descriptionLayoutParams2
                                horizontalLayout2.addView(descriptionTextView2)


                                val editText2 = EditText(this@FavoriteList)
                                editText2.setText(updatedLikePlaceInfos[indexToUpdate].evaluate.toString())
                                editText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText2.setPadding(16, 8, 16, 8)

                                editText2.setBackgroundResource(R.drawable.edittext2)

                                val layoutparams2 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
//                                layoutparams2.weight = 1f
//                                layoutparams2.height = 40
                                layoutparams2.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                                editText2.layoutParams = layoutparams2
                                editText2.setLines(3)
                                horizontalLayout2.addView(editText2)
                                editTextContainer.addView(horizontalLayout2)


                                //備註
                                val horizontalLayout3 = LinearLayout(this@FavoriteList)
                                horizontalLayout3.orientation = LinearLayout.HORIZONTAL
                                val horizontalLayoutParams3 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                horizontalLayout2.layoutParams = horizontalLayoutParams3
                                val descriptionTextView3 = TextView(this@FavoriteList)
                                descriptionTextView3.text = "備註" // 例如，对于评分的 EditText
                                descriptionTextView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                descriptionTextView3.setPadding(16, 8, 16, 8)

                                val descriptionLayoutParams3 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                descriptionLayoutParams3.height = 100
                                descriptionTextView3.layoutParams = descriptionLayoutParams3
                                horizontalLayout3.addView(descriptionTextView3)

                                val editText3 = EditText(this@FavoriteList)
                                editText3.setText(updatedLikePlaceInfos[indexToUpdate].notes.toString())
                                editText3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                                editText3.setPadding(16, 8, 16, 8)

                                editText3.setBackgroundResource(R.drawable.edittext2)

                                val layoutparams3 = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
//                                layoutparams3.weight = 1f
//                                layoutparams3.height = 40
                                layoutparams3.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                                editText3.layoutParams = layoutparams3
                                editText3.setLines(3)
                                horizontalLayout3.addView(editText3)
                                editTextContainer.addView(horizontalLayout3)

                                // 创建一个新的 Button
                                val button = Button(this@FavoriteList)
                                button.text = "update"
                                button.setPadding(16, 8, 16, 8)
                                button.setBackgroundResource(R.drawable.edittext2)
                                // 为按钮设置布局参数
                                val buttonLayoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                buttonLayoutParams.height = 100
                                buttonLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                                button.layoutParams = buttonLayoutParams

                                // 为按钮添加点击事件监听器
                                button.setOnClickListener {
                                        // 处理按钮点击

                                    val userRef = database.child("users").child(userId.toString())
                                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            val user1 = dataSnapshot.getValue(User::class.java)
                                            user1?.let {
                                                val updatedLikePlaceInfos2 =
                                                    it.likePlaceInfos?.toMutableList()
                                                        ?: mutableListOf()
                                                val indexToUpdate2 =
                                                    updatedLikePlaceInfos2.indexOfFirst { placeInfo ->
                                                        placeInfo.placeId == placeId // 用特定的条件定位到要更新的对象
                                                    }

                                                if (indexToUpdate2 != -1) {
                                                    // 获取并更新特定的 PlaceInfo 对象
                                                    Log.d("place", "index: $indexToUpdate2")
                                                    val placeInfoToUpdate = PlaceInfo(
                                                        placeId = placeId,
                                                        notes = editText3.text.toString(),
                                                        rating = editText1.text.toString()
                                                            .toDoubleOrNull()
                                                            ?: 0.0, // 使用 toDoubleOrNull 防止转换错误
                                                        evaluate = editText2.text.toString() // 假设 evaluate 是 PlaceInfo 类的一个字段
                                                    )
                                                    Log.d(
                                                        "place",
                                                        "notes: ${editText3.text}, rating: ${editText1.text}, evaluate: ${editText2.text}"
                                                    )

                                                    // 替换列表中的旧对象
                                                    updatedLikePlaceInfos2[indexToUpdate2] =
                                                        placeInfoToUpdate

                                                    // 将更新后的对象写回数据库
                                                    userRef.updateChildren(mapOf("likePlaceInfos" to updatedLikePlaceInfos2))
                                                }
                                            }
                                        }
                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // 处理错误
                                        }
                                    })
                                }
                                // 将按钮添加到 LinearLayout 或 CardView
                                editTextContainer.addView(button)
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