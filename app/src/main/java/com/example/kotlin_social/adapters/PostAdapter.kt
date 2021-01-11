package com.example.kotlin_social.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.kotlin_social.R
import com.example.kotlin_social.data.entities.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_post.view.*
import java.lang.System.load
import javax.inject.Inject

class PostAdapter @Inject constructor(
        private val glide: RequestManager
) : PagingDataAdapter<Post, PostAdapter.PostViewHolder>(Companion){

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage: ImageView = itemView.ivPostImage
        val ivAuthorProfileImage: ImageView = itemView.ivAuthorProfileImage
        val tvPostAuthor: TextView = itemView.tvPostAuthor
        val tvPostText: TextView = itemView.tvPostText
        val tvLikedBy: TextView = itemView.tvLikedBy
        val ibLike: ImageButton = itemView.ibLike
        val ibComments: ImageButton = itemView.ibComments
        val ibDeletePost: ImageButton = itemView.ibDeletePost
    }

    companion object: DiffUtil.ItemCallback<Post>() {

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.apply {
            glide.load(post.imageUrl).into(ivPostImage)
            glide.load(post.authorProfilePictureUrl).into(ivAuthorProfileImage)
            tvPostAuthor.text = post.authorUsername
            tvPostText.text = post.text
            val likeCount = post.likedBy.size
            tvLikedBy.text = when {
                likeCount <= 0 -> "No likes"
                likeCount == 1 -> "Liked by 1 person"
                else -> "Liked by $likeCount person"
            }
            val uid = FirebaseAuth.getInstance().uid!!
            ibDeletePost.isVisible = uid == post.authorId
            ibLike.setImageResource(if(post.isLiked) {
                R.drawable.ic_like
            } else R.drawable.ic_like_white)

            tvPostAuthor.setOnClickListener {
                onUserClikListener?.let {click ->
                    click(post.authorId)
                }
            }

            ivAuthorProfileImage.setOnClickListener {
                onUserClikListener?.let {click ->
                    click(post.authorId)
                }
            }

            tvLikedBy.setOnClickListener {
                onLikedByClikListener?.let {click ->
                    click(post)
                }
            }

            ibLike.setOnClickListener {
                onLikeClikListener?.let {click ->
                    if(!post.isLiking) {
                        click(post, holder.layoutPosition)
                    }
                }
            }

            ibComments.setOnClickListener {
                onCommentsClikListener?.let { click ->
                    click(post)
                }
            }

            ibDeletePost.setOnClickListener {
                onDeletePostClikListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    private var onLikeClikListener: ((Post, Int) -> Unit)? = null
    private var onUserClikListener: ((String) -> Unit)? = null
    private var onDeletePostClikListener: ((Post) -> Unit)? = null
    private var onLikedByClikListener: ((Post) -> Unit)? = null
    private var onCommentsClikListener: ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClikListener = listener
    }

    fun setOnUserClikListener(listener: (String) -> Unit) {
        onUserClikListener = listener
    }

    fun setOnDeletePostClikListener(listener: (Post) -> Unit) {
        onDeletePostClikListener = listener
    }

    fun setOnLikedByClikListener(listener: (Post) -> Unit) {
        onLikedByClikListener = listener
    }

    fun setOnCommentsClikListener(listener: (Post) -> Unit) {
        onCommentsClikListener = listener
    }

}