package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.view.animation.AnimationUtils
import android.widget.ImageView

class Spin : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_spin)

                val rouletteImageView: ImageView = findViewById(R.id.plate)
                val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
                rouletteImageView.startAnimation(rotateAnimation)

                fun onSpinButtonClick(view: View) {}

        }
}