package com.example.kotlin_social.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.kotlin_social.R
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.entities.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_user.view.*
import java.lang.System.load
import javax.inject.Inject

class UserAdapter @Inject constructor(
        private val glide: RequestManager
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePicture: ImageView = itemView.ivProfileImage
        val tvUsername: TextView = itemView.tvUsername
    }

    private val diffCallback = object: DiffUtil.ItemCallback<User>() {

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    val user = users[position]
        holder.apply{
            glide.load(user.profilePictureUrl).into(ivProfilePicture)
            tvUsername.text = user.username
            itemView.setOnClickListener {
                onUserClikListener?.let{ click ->
                    click(user)
                }
            }
        }
    }

    private var onUserClikListener: ((User) -> Unit)? = null

    fun setOnUserClikListener(listener: (User) -> Unit) {
        onUserClikListener = listener
    }

}