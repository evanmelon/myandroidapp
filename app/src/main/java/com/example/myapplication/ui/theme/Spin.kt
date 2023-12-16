package com.example.myapplication.ui.theme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.myapplication.R
import android.view.animation.AnimationUtils

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