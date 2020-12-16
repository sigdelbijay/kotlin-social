package com.example.kotlin_social.data.entities

import com.example.kotlin_social.other.Constants
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User (
    val uid: String = "",
    val username: String = "",
    var profilePictureUrl: String = Constants.DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(),
    @Exclude
    var isFollowing: Boolean = false



)