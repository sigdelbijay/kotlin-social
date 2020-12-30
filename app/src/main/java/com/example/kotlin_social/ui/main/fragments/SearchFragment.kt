package com.example.kotlin_social.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin_social.R
import com.example.kotlin_social.adapters.UserAdapter
import com.example.kotlin_social.other.Constants
import com.example.kotlin_social.other.EventObserver
import com.example.kotlin_social.ui.main.viewmodels.SearchViewModel
import com.example.kotlin_social.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    @Inject
    lateinit var userAdapter: UserAdapter

    private val viewModel : SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_USERS_TIME_DELAY)
                editable?.let{
                        viewModel.searchUser(editable.toString())
                }
            }
        }

        userAdapter.setOnUserClikListener {user ->
            findNavController().navigate(
                    SearchFragmentDirections.globalActionToOthersProfileFragment(user.uid)
            )
        }
    }


    private fun setupRecyclerView() = rvSearchResults.apply{
        adapter = userAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
                onError = {
                    searchProgressBar.isVisible = false
                    snackbar(it)
                },
                onLoading = { searchProgressBar.isVisible = true }
        ){ users ->
            searchProgressBar.isVisible = true
            userAdapter.users = users

        })
    }
}