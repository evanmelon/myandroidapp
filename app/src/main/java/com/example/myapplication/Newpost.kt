package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Newpost : AppCompatActivity() {
    private lateinit var readWriteSnippets: ReadAndWriteSnippets
    private lateinit var Title : EditText
    private lateinit var Content: EditText
//    val data: Data = applicationContext as Data
    private lateinit var addpostButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpost)
        byId()
        sendpost()
        readWriteSnippets = ReadAndWriteSnippets()
        readWriteSnippets.initializeDbRef()
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
            readWriteSnippets.writeNewPost(userId = userId.toString(), username = name.toString(), title = Tstr, body = Cstr)
//            data.userdata?.writeNewPost(userId = userId.toString(), username = name.toString(), title = Tstr, body = Cstr)
//            Post(userId.toString(), name.toString(), Tstr, Cstr)
            // 回到個人頁面
            val intent = Intent(this, Personal::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}
