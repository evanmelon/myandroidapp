package com.example.myapplication

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_profile)

        val profileImageView: ImageView = findViewById(R.id.profileImageView)

        // 设置头像、用户名、关注者数量、关注数量和帖子数量
        profileImageView.setImageResource(R.drawable.avatar)

        val signButton: Button = findViewById(R.id.signinButton)
        signButton.setOnClickListener {

            val intent = Intent(this, FirebaseUIActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }
}