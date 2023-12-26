package com.example.myapplication

import android.app.ActivityOptions
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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val container = findViewById<LinearLayout>(R.id.postsContainer)
        val userPostsRef = Firebase.database.reference.child("posts")
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posts = dataSnapshot.children.mapNotNull { it.getValue(Post::class.java) }
                posts.forEach { post ->
                    Log.d("Home: post", "title: ${post.title}, body: ${post.body}, star: ${post.stars}")
                    // 这里可以访问 post 的属性，如 post.title 和 post.body

                    // 動態新增貼文
                    val cardView = CardView(this@Home)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        400
                    )
                    layoutParams.setMargins(16, 16, 16, 16)

                    cardView.layoutParams = layoutParams
                    cardView.cardElevation = 5f

                    // 貼文
                    val textView = TextView(this@Home)
                    val text = "Title: ${post.title}\nBody: ${post.body}"
                    textView.text = text
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    textView.setPadding(16, 16, 16, 16)
                    // 将TextView添加到CardView中
                    cardView.addView(textView)

                    // 作者
                    val textview2 = TextView(this@Home)
                    val text2 = "${post.author}"
                    textview2.text = text2
                    textview2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    textview2.setPadding(16, 16, 16, 16)

                    container.addView(textview2)
                    // 将CardView添加到容器中
                    container.addView(cardView)

                }
                // 处理帖子数量，例如更新UI
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })

        val proButton: Button = findViewById(R.id.profileButton)
        proButton.setOnClickListener {

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