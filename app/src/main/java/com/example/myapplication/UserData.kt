package com.example.myapplication

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

// User(userId, email)
data class User(
    val userId : String,
    val email : String
)
//Post(userId, title, body)
data class Post(
    val userId : String,
    val title: String,
    val body : String
){
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["userId"] = userId
        result["title"] = title
        result["body"] = body
        return result
    }
}
abstract class UserData {

    // Firebase Database 根節點
    private val mDatabase : DatabaseReference = FirebaseDatabase.getInstance().reference
    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    // 可以放東西進到suerID
    val mUserRef : DatabaseReference= mDatabase.child("User_database").child(userId)

    // [START declare_database_ref]
    private lateinit var database: DatabaseReference
    // [END declare_database_ref]

    fun initializeDbRef() {
        // [START initialize_database_ref]
        database = Firebase.database.reference
        // [END initialize_database_ref]
    }

    // 新增用戶
    fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name, email)

        database.child("users").child(userId).setValue(user)
    }

    // 可以知道資料何時提交
    fun writeNewUserWithTaskListeners(userId: String, email: String) {
        val user = User(userId, email)

        // [START rtdb_write_new_user_task]
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                // Write was successful!
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
        // [END rtdb_write_new_user_task]
    }

    // 讀取偵聽器，同步資料庫的更新
    private fun addPostEventListener(postReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            // 讀取給定路徑中內容
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue<Post>()
            }
            //
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
    }

    // 建立新貼文及更新
    private fun writeNewPost(userId: String, title: String, body: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = database.child("posts").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val post = Post(userId, title, body)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/posts/$key" to postValues,
            "/user-posts/$userId/$key" to postValues,
        )
        database.updateChildren(childUpdates)
    }

}