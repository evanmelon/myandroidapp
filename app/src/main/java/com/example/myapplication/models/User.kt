package com.example.myapplication.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// [START rtdb_user_class]
@IgnoreExtraProperties
data class User(
    var uid: String? = null,
    val username: String? = null,
    val email: String? = null,
    val promsg: String? = null

) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "email" to email,
            "promsg" to promsg
        )
    }
    // [END post_to_map]
}
