package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.Window
import android.widget.ImageView
import android.widget.TextView

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        val profileImageView: ImageView = findViewById(R.id.profileImageView)
        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        val followersTextView: TextView = findViewById(R.id.followersTextView)
        val followingTextView: TextView = findViewById(R.id.followingTextView)
        val postsTextView: TextView = findViewById(R.id.postsTextView)

        // 设置头像、用户名、关注者数量、关注数量和帖子数量
        profileImageView.setImageResource(R.drawable.avatar)
        usernameTextView.text = "YourUsername"
        followersTextView.text = "Followers: 100"
        followingTextView.text = "Following: 50"
        postsTextView.text = "Posts: 200"
    }
}