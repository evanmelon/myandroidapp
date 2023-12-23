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
import android.widget.EditText

class Spin : AppCompatActivity() {
        private lateinit var uploadButton: Button
        private lateinit var textView: TextView
        private lateinit var editText: EditText
        private lateinit var turntable: Turntable
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                supportActionBar?.hide()
                setContentView(R.layout.activity_spin)
                turntable = findViewById(R.id.turntable)
                turntable.startTurn("1", 5)
                byId() // 綁定元件
                updateData() // 上傳資料到 TextView

        }
        // 綁定元件
        private fun byId() {
                uploadButton = findViewById(R.id.uploadButton)
                textView = findViewById(R.id.textView)
                editText = findViewById(R.id.editText)
        }

        // 上傳資料到 TextView
        private fun updateData() {
                uploadButton.setOnClickListener {
                        val str = editText.text.toString() // 拿取 EditText 字串
                        if(str.isNotEmpty()) {
                                editText.text.clear() // 清除輸入框的資料
                                textView.text = str // 設定文字到 TextView
                                var selectList = str.split(",").toMutableList() // 將文字存入一個LIST
                                turntable.setSelectList((selectList))
                        }
                }
        }
//        fun onSpinButtonClick(view: View) {
////                val rouletteImageView: ImageView = findViewById(R.id.plate)
//                val degrees = Random.nextInt(3600)
//
//                val rotateAnimation = RotateAnimation(
//                        0f,
//                        -degrees.toFloat(),
//                        Animation.RELATIVE_TO_SELF,
//                        0.5f,
//                        Animation.RELATIVE_TO_SELF,
//                        0.5f
//                )
//                rotateAnimation.duration = 3000 // 3 seconds
//                rotateAnimation.fillAfter = true
//
////                rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
////                        override fun onAnimationStart(animation: Animation?) {
////                                // Disable the start button during animation
////                                rouletteImageView.isEnabled = false
////                        }
////                        override fun onAnimationEnd(animation: Animation?) {
////                                // Enable the start button after animation ends
////                                rouletteImageView.isEnabled = true
////                        }
////                        override fun onAnimationRepeat(animation: Animation?) {
////                                // Not used
////                        }
////                })
//
////                rouletteImageView.startAnimation(rotateAnimation)
////                textLabel.startAnimation((rotateAnimation))
//
//        }
}