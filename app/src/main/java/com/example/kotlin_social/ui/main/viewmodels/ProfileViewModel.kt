package com.example.kotlin_social.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.entities.User
import com.example.kotlin_social.data.pagingsource.ProfilePostsPagingSource
import com.example.kotlin_social.other.Constants
import com.example.kotlin_social.other.Event
import com.example.kotlin_social.other.Resource
import com.example.kotlin_social.repositories.MainRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {

    private val _profileMeta = MutableLiveData<Event<Resource<User>>>()
    val profileMeta : LiveData<Event<Resource<User>>> = _profileMeta

    private val _followStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followStatus : LiveData<Event<Resource<Boolean>>> = _followStatus

    fun toggleFollowForUser(uid: String) {
        _followStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun loadProfile(uid: String) {
        _profileMeta.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.getUser(uid)
            _profileMeta.postValue(Event(result))
        }
    }

    fun getPagingFlow(uid: String): Flow<PagingData<Post>> {
        val pagingSource = ProfilePostsPagingSource(
            FirebaseFirestore.getInstance(),
            uid
        )
        return Pager(PagingConfig(Constants.PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }
}