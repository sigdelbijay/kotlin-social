package com.example.kotlin_social.ui.main.fragments
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.example.kotlin_social.adapters.PostAdapter
import com.example.kotlin_social.other.EventObserver
import com.example.kotlin_social.ui.main.viewmodels.BasePostViewModel
import com.example.kotlin_social.ui.snackbar
import javax.inject.Inject

abstract class BasePostFragment(
        layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter

    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                postProgressBar.isVisible = false
                snackbar(it)
            },
                onLoading = {postProgressBar.isVisible = true }
        ){ posts ->
            postProgressBar.isVisible = false
            postAdapter.posts = posts
        })
    }

}