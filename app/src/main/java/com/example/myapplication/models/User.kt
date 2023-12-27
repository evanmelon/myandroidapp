package com.example.myapplication.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// [START rtdb_user_class]
//data class PlaceInfo(
//    val placeId: String?,
//    val rating: Double?,  // 评价
//    val notes: String?    // 备注
//)
@IgnoreExtraProperties
data class User(
    val username: String? = null,
    val email: String? = null,
    val promsg: String? = null,
    val likePlaceIds: List<String>? = null,
    val followers: List<String>? = null,
    val following: List<String>? = null,
//    val likePlaceInfos: List<PlaceInfo>? = null

) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "username" to username,
            "email" to email,
            "promsg" to promsg,
            "likePlaceIds" to likePlaceIds,
            "followers" to followers,
            "following" to following,
//            "likePlaceInfos" to likePlaceInfos
        )
    }
    // [END post_to_map]
}
