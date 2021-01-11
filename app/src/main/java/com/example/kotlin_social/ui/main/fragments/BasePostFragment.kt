package com.example.kotlin_social.ui.main.fragments
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.kotlin_social.R
import com.example.kotlin_social.adapters.PostAdapter
import com.example.kotlin_social.adapters.UserAdapter
import com.example.kotlin_social.dialogs.DeletePostDialog
import com.example.kotlin_social.dialogs.LikedByDialog
import com.example.kotlin_social.other.EventObserver
import com.example.kotlin_social.ui.main.viewmodels.BasePostViewModel
import com.example.kotlin_social.ui.snackbar
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract class BasePostFragment(
    layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter

    //handle individaully in home and profile fragment
//    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    private var curLikedIndex: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        postAdapter.setOnLikeClickListener { post, i ->
            curLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggelLikeForPost(post)
        }

        postAdapter.setOnDeletePostClikListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }

        postAdapter.setOnLikedByClikListener { post ->
            basePostViewModel.getUsers(post.likedBy)
        }

        postAdapter.setOnCommentsClikListener { comment ->
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply {
                    putString("postId", comment.id)
                }
            )
        }
    }

    private fun subscribeToObservers() {
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                curLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                snackbar(it)
            },
            onLoading = {
                curLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = true
                    postAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            curLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.peek(index)?.apply {
                    this.isLiked = isLiked
                    this.isLiking = false
                    if(isLiked) {
                        likedBy += uid
                    } else {
                        likedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(index)
            }
        })
        basePostViewModel.likedByUsers.observe(viewLifecycleOwner, EventObserver(
            onError = {snackbar(it)}
        ){ users ->
            val userAdapter = UserAdapter(glide)
            userAdapter.users = users
            LikedByDialog(userAdapter).show(childFragmentManager, null)
        })

        //paging 3 library handles this
//        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
//            onError = {
//                postProgressBar.isVisible = false
//                snackbar(it)
//            },
//            onLoading = {
//                postProgressBar.isVisible = true
//            }
//        ) { posts ->
//            postProgressBar.isVisible = false
//            postAdapter.posts = posts
//        })
    }
}