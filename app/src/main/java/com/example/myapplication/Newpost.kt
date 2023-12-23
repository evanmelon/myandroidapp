package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Newpost : AppCompatActivity() {
    private lateinit var Title : EditText
    private lateinit var Content: EditText
//    val data: Data = applicationContext as Data
    val addpostButton : Button = findViewById(R.id.addpost)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpost)
        byId()
        sendpost()
    }
    private fun byId() {
        Title = findViewById(R.id.Title)
        Content = findViewById(R.id.Content)
    }

    private fun sendpost() {
        addpostButton.setOnClickListener {
            val sharedPreferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)
            val name = sharedPreferences.getString("name", null)
            val email = sharedPreferences.getString("email", null)
            val Tstr = Title.text.toString()    // 拿取 Title 字串
            val Cstr = Content.text.toString()  // 拿取 Content 字串
            // 更新資料庫
//            data.userdata?.writeNewPost(userId = userId.toString(), username = name.toString(), title = Tstr, body = Cstr)
//            Post(userId.toString(), name.toString(), Tstr, Cstr)
            // 回到個人頁面
            val intent = Intent(this, Personal::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}
