package com.bahri.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahri.storyapp.data.DataRepository
import com.bahri.storyapp.data.remote.model.home.ResponseHome
import com.bahri.storyapp.utils.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel constructor(private val dataRepository: DataRepository): ViewModel() {

    private val listStory = MutableLiveData<NetworkResult<ResponseHome>>()
    private var job: Job? = null

    fun fetchListStory(auth: String) {
        job = viewModelScope.launch {
            dataRepository.getStories(auth).collectLatest {
                listStory.value = it
            }
        }
    }

    val responseListStory: LiveData<NetworkResult<ResponseHome>> = listStory


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}