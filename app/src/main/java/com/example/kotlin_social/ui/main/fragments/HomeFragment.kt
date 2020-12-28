package com.example.kotlin_social.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_social.R
import com.example.kotlin_social.ui.main.viewmodels.BasePostViewModel
import com.example.kotlin_social.ui.main.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : BasePostFragment(R.layout.fragment_home) {
    override val postProgressBar: ProgressBar
        get() = allPostsProgressBar
    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() = rvAllPosts.apply {
        adapter = adapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}