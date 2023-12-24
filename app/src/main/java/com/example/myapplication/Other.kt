package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var OtherUsername: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)
        val name = sharedPref.getString("NAME", null)
        val email = sharedPref.getString("EMAIL", null)
        val container = findViewById<LinearLayout>(R.id.postContainer) // 假设你有一个包含CardView的LinearLayout，其ID为container
        container.removeAllViews()
        // 資料庫抓資料
        val userPostsRef = Firebase.database.reference.child("user-posts").child(userId.toString())
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
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })

        OtherUsername = findViewById(R.id.OtherUser)
        OtherUsername.text = name

        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {

            val intent = Intent(this, Profile::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        val mapButton: Button = findViewById(R.id.map)
        mapButton.setOnClickListener {

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}