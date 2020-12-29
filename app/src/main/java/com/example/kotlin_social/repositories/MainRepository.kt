package com.example.kotlin_social.repositories

import android.net.Uri
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.entities.User
import com.example.kotlin_social.other.Resource

interface MainRepository {
    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun getUsers(uids: List<String>) : Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostForFollows(): Resource<List<Post>>

    suspend fun getPostForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun searchUser(query: String): Resource<List<User>>

}