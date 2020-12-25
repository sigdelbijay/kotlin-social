package com.example.kotlin_social.repositories

import android.net.Uri
import com.example.kotlin_social.other.Resource

interface MainRepository {
    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>
}