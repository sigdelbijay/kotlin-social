package com.example.kotlin_social.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.pagingsource.FollowPostsPagingSource
import com.example.kotlin_social.other.Constants
import com.example.kotlin_social.other.Event
import com.example.kotlin_social.other.Resource
import com.example.kotlin_social.repositories.MainRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher){

    val pagingFlow = Pager(PagingConfig(Constants.PAGE_SIZE)) {
        FollowPostsPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)

}