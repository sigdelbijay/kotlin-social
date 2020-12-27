package com.example.kotlin_social.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlin_social.data.entities.Post
import com.example.kotlin_social.data.entities.User
import com.example.kotlin_social.other.Event
import com.example.kotlin_social.other.Resource
import com.example.kotlin_social.repositories.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BasePostViewModel (
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel(){
    private val _likePostStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val likePostStatus : LiveData<Event<Resource<Boolean>>> = _likePostStatus

    private val _deletePostStatus = MutableLiveData<Event<Resource<Post>>>()
    val deletePostStatus : LiveData<Event<Resource<Post>>> = _deletePostStatus

    private val _likedByUsers = MutableLiveData<Event<Resource<List<User>>>>()
    val likedByUsers : LiveData<Event<Resource<List<User>>>> = _likedByUsers

    abstract val posts: LiveData<Event<Resource<List<Post>>>>
    abstract fun getPosts(uid: String = "")

    fun getUsers(uids: List<String>) {
        _likedByUsers.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.getUsers(uids)
            _likedByUsers.postValue(Event(result))
        }
    }

    fun toggelLikeForPost(post: Post) {
        _likePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.toggleLikeForPost(post)
            _likePostStatus.postValue(Event(result))
        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.deletePost(post)
            _deletePostStatus.postValue(Event(result))
        }
    }

}