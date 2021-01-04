package com.example.kotlin_social.repositories

import android.net.Uri
import com.example.kotlin_social.data.entities.Comment
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.entities.User
import com.example.kotlin_social.other.Resource
import com.example.kotlin_social.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

@ActivityScoped
class DefaultMainRepository : MainRepository{
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = fireStore.collection("users")
    private val posts = fireStore.collection("posts")
    private val comments = fireStore.collection("comments")

    override suspend fun createPost(imageUri: Uri, text: String) = withContext(Dispatchers.IO){
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl = imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(
                    id = postId,
                    authorId = uid,
                    text = text,
                    imageUrl = imageUrl,
                    date = System.currentTimeMillis()
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }

    override suspend fun getUsers(uids: List<String>) = withContext(Dispatchers.IO) {
        safeCall {
            val userList = users.whereIn("uid", uids).orderBy("username")
                .get().await().toObjects(User::class.java)
            Resource.Success(userList)
        }
    }

    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!! //auth.uid!!
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)

        }
    }

    override suspend fun getPostForFollows() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val follows = getUser(uid).data!!.follows
            val allPosts = posts.whereIn("authorId", follows)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    val user = getUser(post.authorId).data!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy
                }
            Resource.Success(allPosts)
        }
    }

    override suspend fun getPostForProfile(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = getUser(uid).data!!
            //val currentUid = auth.uid!!
            //post.isLiked = currentUid in post.likedBy
            val allPosts = posts.whereEqualTo("authorId", uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Post::class.java)
                    .onEach{post ->
                        post.authorProfilePictureUrl = user.profilePictureUrl
                        post.authorUsername = user.username
                        post.isLiked = user.uid in post.likedBy
                    }
            Resource.Success(allPosts)

        }
    }

    override suspend fun toggleLikeForPost(post: Post) = withContext(Dispatchers.IO){
        safeCall {
            var isLiked = false
            fireStore.runTransaction { transaction ->
                val uid = auth.uid!!
                val postResult = transaction.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                transaction.update(
                    posts.document(post.id),
                    "likedBy",
                    if(uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid
                    }
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    override suspend fun toggleFollowForUser(uid: String)= withContext(Dispatchers.IO) {
        safeCall {
            var isFollowing = false
            fireStore.runTransaction { transaction ->
                val currentUid = auth.uid!!
                val currentUser = transaction.get(users.document(currentUid)).toObject(User::class.java)!!
                isFollowing = uid in currentUser.follows
                val newFollows = if(isFollowing) currentUser.follows - uid else currentUser.follows + uid
                transaction.update(users.document(currentUid), "follows", newFollows)
            }.await()
            Resource.Success(!isFollowing)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResults = users.whereGreaterThanOrEqualTo("username", query.toUpperCase(Locale.ROOT))
                    .get().await().toObjects(User::class.java)
            Resource.Success(userResults)
        }
    }

    override suspend fun createComment(commentText: String, postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val commentId = UUID.randomUUID().toString()
            val user = getUser(uid).data!!
            val comment = Comment(
                commentId,
                postId,
                uid,
                user.username,
                user.profilePictureUrl,
                commentText
            )
            comments.document(commentId).set(comment).await()
            Resource.Success(comment)
        }
    }

    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            Resource.Success(comment)
        }
    }

    override suspend fun getCommentsForPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentList = comments
                .whereEqualTo("postId", postId)
                .orderBy("date")
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.username = user.username
                    comment.profilePictureUrl = user.profilePictureUrl

                }
            Resource.Success(commentList)
        }
    }
}