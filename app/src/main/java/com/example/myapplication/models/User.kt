package com.example.myapplication.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// [START rtdb_user_class]
@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val email: String? = null,
    val promsg: String? = null

) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "email" to email,
            "promsg" to promsg
        )
    }
    // [END post_to_map]
}
