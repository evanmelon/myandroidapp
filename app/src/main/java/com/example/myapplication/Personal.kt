package com.example.myapplication

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Personal: AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var posts: TextView
    private lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("USER_ID", null)
        val name = sharedPref.getString("NAME", null)
        val email = sharedPref.getString("EMAIL", null)
        val userPostsRef = Firebase.database.reference.child("user-posts").child(userId.toString())
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts = findViewById(R.id.posts)
                val postCount = dataSnapshot.childrenCount.toInt() // 获取帖子数量
                posts.text = postCount.toString()
                // 处理帖子数量，例如更新UI
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })

        userName = findViewById(R.id.UserName)
        userName.text = name
        val homeButton: Button = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {

            val intent = Intent(this, Home::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        val mapButton: Button = findViewById(R.id.map)
        mapButton.setOnClickListener {

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        // 新增貼文
        val postButton: Button = findViewById(R.id.newpost)
        postButton.setOnClickListener {

            val intent = Intent(this, Newpost::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        // 使用者輸入個人簡介
        val textmsg:TextView = findViewById(R.id.textmsg)
        val editButton: Button = findViewById(R.id.Edit)
        editButton.setOnClickListener {
            val editText = EditText(this)
            val dialog = AlertDialog.Builder(this)
                .setTitle("輸入文字")
                .setView(editText)
                .setPositiveButton("確定") { _, _ ->
                    val userInput = editText.text.toString()
                    textmsg.text = userInput
                }
                .setNegativeButton("取消", null)
                .create()

            dialog.show()
        }
    }
}