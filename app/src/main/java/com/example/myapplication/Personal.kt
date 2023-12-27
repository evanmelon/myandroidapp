package com.example.myapplication

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.models.Post
import com.google.firebase.auth.ktx.auth
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
    private lateinit var msg: TextView
    private lateinit var readWriteSnippets: ReadAndWriteSnippets
    private lateinit var sharedPref: android.content.SharedPreferences
    private var userId: String? = null
    private var email: String? = null
    private var name: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        readWriteSnippets = ReadAndWriteSnippets()
        readWriteSnippets.initializeDbRef()
        database = Firebase.database.reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        email = sharedPref.getString("EMAIL", null)
        name = sharedPref.getString("NAME", null)
        val container = findViewById<LinearLayout>(R.id.postContainer)
        container.removeAllViews()
        // 資料庫抓post
        val userPostsRef = Firebase.database.reference.child("user-posts").child(userId.toString())
        userPostsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts = findViewById(R.id.posts)
                val postCount = dataSnapshot.childrenCount.toInt() // 获取帖子数量
                posts.text = postCount.toString()
                val posts = dataSnapshot.children.mapNotNull { it.getValue(Post::class.java) }
                posts.forEach { post ->
                    Log.d("post", "title: ${post.title}, body: ${post.body}, star: ${post.stars}")
                    // 这里可以访问 post 的属性，如 post.title 和 post.body

                    // 動態新增貼文
                    val cardView = CardView(this@Personal)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        400
                    )
                    layoutParams.setMargins(16, 16, 16, 16)

                    cardView.layoutParams = layoutParams
                    cardView.cardElevation = 5f

                    val textView = TextView(this@Personal)
                    val text = "Title: ${post.title}\nBody: ${post.body}"
                    textView.text = text
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    textView.setPadding(16, 16, 16, 16)

                    // 将TextView添加到CardView中
                    cardView.addView(textView)

                    // 将CardView添加到容器中
                    container.addView(cardView)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // 处理错误
            }
        })
//        // 資料庫抓簡介
//        val userRef = Firebase.database.reference.child("users").child(userId.toString())
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot)
//            {
//                val promsgs = dataSnapshot.children.mapNotNull { it.getValue(User::class.java) }
//                promsgs.forEach{user ->
//
//                    msg = findViewById(R.id.MSG)
//                    msg.text = user.promsg.toString()
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                // 处理错误
//            }
//        })


        userName = findViewById(R.id.UserName)
        userName.text = name
        Log.i("firebase", "userid: ${userId.toString()}")
        database.child("users").child(userId.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val dataSnapshotMap = it.value as Map<String, Any>
            msg = findViewById(R.id.MSG)
            msg.text = dataSnapshotMap["promsg"] as String
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

//        msg.text =
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
        val logoutButton: Button = findViewById(R.id.Logout)
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.remove("MyApp")
            editor.apply()
            val intent = Intent(this, FirebaseUIActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        val likeButton: ImageView = findViewById(R.id.Likelist)
        likeButton.setOnClickListener {

            val intent = Intent(this, FavoriteList::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        // 使用者輸入個人簡介
        val textmsg:TextView = findViewById(R.id.MSG)
        val editButton: Button = findViewById(R.id.Edit)
        editButton.setOnClickListener {
            val editText = EditText(this)
            val dialog = AlertDialog.Builder(this)
                .setTitle("輸入文字")
                .setView(editText)
                .setPositiveButton("確定") { _, _ ->
                    val userInput = editText.text.toString()
                    textmsg.text = userInput
                    database.child("users").child(userId.toString()).child("promsg").setValue(textmsg.text)
                    Log.d("msg", "set msg to $textmsg.text")
                }
                .setNegativeButton("取消", null)
                .create()
                dialog.show()

//            readWriteSnippets.writeNewPro(userId = userId.toString(), username = name.toString(), email = email.toString(), promsg = editText.text.toString())

            }
        }
    override fun onStart() {
        super.onStart()
        sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        email = sharedPref.getString("EMAIL", null)
        name = sharedPref.getString("NAME", null)
        Log.d("personal", "name: $name")
        userName = findViewById(R.id.UserName)
        userName.text = name
        // 在这里执行一些在 Activity 开始变得对用户可见时需要进行的操作

        // 例如，可以在这里处理一些 UI 更新、初始化数据等操作
    }
}
