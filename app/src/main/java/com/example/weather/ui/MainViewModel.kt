package com.example.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weather.Repository.Repository
import com.example.weather.utils.ApiManager
import com.example.weather.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel :ViewModel() {
    val repository  = Repository(ApiManager.getwebbservices())

    suspend fun getLocationDetails(q:Double , q1:Double) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(repository.FetchLocationDetails(q,q1)))

        } catch (e:Exception){
                emit(Resource.error(null , e.message.toString()))
        }
    }
}