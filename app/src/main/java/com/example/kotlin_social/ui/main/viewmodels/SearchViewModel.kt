package com.example.kotlin_social.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_social.data.entities.User
import com.example.kotlin_social.other.Event
import com.example.kotlin_social.other.Resource
import com.example.kotlin_social.repositories.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
        private val repository: MainRepository,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _searchResults = MutableLiveData<Event<Resource<List<User>>>>()
    val searchResults : LiveData<Event<Resource<List<User>>>> = _searchResults

    fun searchUser(query: String) {
        if(query.isEmpty()) return

        _searchResults.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.searchUser(query)
            _searchResults.postValue(Event(result))
        }
    }
}