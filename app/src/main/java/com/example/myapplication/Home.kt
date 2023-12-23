package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        myRef.setValue("嗨你好")
        val userPostsRef = Firebase.database.reference.child("posts")
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posts = dataSnapshot.children.mapNotNull { it.getValue(Post::class.java) }
                posts.forEach { post ->
                    Log.d("Home: post", "title: ${post.title}, body: ${post.body}, star: ${post.stars}")
                    // 这里可以访问 post 的属性，如 post.title 和 post.body
                }
                // 处理帖子数量，例如更新UI
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })
    }
}