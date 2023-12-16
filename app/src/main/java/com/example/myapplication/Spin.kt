package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import android.graphics.Canvas

class Spin : AppCompatActivity() {
        private lateinit var textLabel: TextView
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_spin)
                textLabel = findViewById(R.id.textLabel)
                val turntable: Turntable = findViewById(R.id.turntable)
                turntable.startTurn("1", 5)
        }
        fun onSpinButtonClick(view: View) {
                val rouletteImageView: ImageView = findViewById(R.id.plate)
                val degrees = Random.nextInt(3600)

                val rotateAnimation = RotateAnimation(
                        0f,
                        -degrees.toFloat(),
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                )
                rotateAnimation.duration = 3000 // 3 seconds
                rotateAnimation.fillAfter = true

                rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                                // Disable the start button during animation
                                rouletteImageView.isEnabled = false
                        }
                        override fun onAnimationEnd(animation: Animation?) {
                                // Enable the start button after animation ends
                                rouletteImageView.isEnabled = true
                        }
                        override fun onAnimationRepeat(animation: Animation?) {
                                // Not used
                        }
                })

                rouletteImageView.startAnimation(rotateAnimation)
                textLabel.startAnimation((rotateAnimation))

        }
}