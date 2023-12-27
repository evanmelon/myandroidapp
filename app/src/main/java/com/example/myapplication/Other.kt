package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.models.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Other : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var posts: TextView
    private lateinit var otherUsername: TextView
    private lateinit var msg: TextView
    private var otherUserID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        otherUserID = intent.getStringExtra("otherID")
        setContentView(R.layout.activity_other)
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)
        val name = sharedPref.getString("NAME", null)
        val email = sharedPref.getString("EMAIL", null)
        val container = findViewById<LinearLayout>(R.id.postContainer) // 假设你有一个包含CardView的LinearLayout，其ID为container
        container.removeAllViews()
        // 資料庫抓資料
        val userPostsRef = Firebase.database.reference.child("user-posts").child(otherUserID.toString())
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts = findViewById(R.id.posts)
                val postCount = dataSnapshot.childrenCount.toInt() // 获取帖子数量
                posts.text = postCount.toString()
                val posts = dataSnapshot.children.mapNotNull { it.getValue(Post::class.java) }
                posts.forEach { post ->
                    Log.d("post", "title: ${post.title}, body: ${post.body}, star: ${post.stars}")
                    // 这里可以访问 post 的属性，如 post.title 和 post.body

                    // 動態新增貼文
                    val cardView = CardView(this@Other)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        400
                    )
                    layoutParams.setMargins(16, 16, 16, 16)

                    cardView.layoutParams = layoutParams
                    cardView.cardElevation = 5f

                    val textView = TextView(this@Other)
                    val text = "Title: ${post.title}\nBody: ${post.body}"
                    textView.text = text
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    textView.setPadding(16, 16, 16, 16)

                    // 将TextView添加到CardView中
                    cardView.addView(textView)

                    // 将CardView添加到容器中
                    container.addView(cardView)
                }
                // 处理帖子数量，例如更新UI

                // 個人簡介

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })

        // 資料庫抓簡介
//        val userRef = Firebase.database.reference.child("users").child(userId.toString())
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot)
//            {
//                val promsgs = dataSnapshot.children.mapNotNull { it.getValue(User::class.java) }
//                promsgs.forEach{user ->
//                    msg = findViewById(R.id.MSG)
//                    msg.text = user.promsg.toString()
//                }
//
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                // 处理错误
//            }
//        })
        database.child("users").child(otherUserID.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val dataSnapshotMap = it.value as Map<String, Any>
            msg = findViewById(R.id.MSG)
            msg.text = dataSnapshotMap["promsg"] as String
            otherUsername = findViewById(R.id.OtherUser)
            otherUsername.text = dataSnapshotMap["username"] as String
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
//        val followButton: Button = findViewById(R.id.followButton)
//        followButton.setOnClickListener {
//
//        }


        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {

            val intent = Intent(this, Personal::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        val mapButton: Button = findViewById(R.id.map)
        mapButton.setOnClickListener {

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}