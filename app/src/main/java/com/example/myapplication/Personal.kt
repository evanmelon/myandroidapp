package com.example.myapplication

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Personal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)

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