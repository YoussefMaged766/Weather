package com.example.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weather.Repository.Repository
import com.example.weather.utils.ApiManager
import com.example.weather.utils.Resource
import com.example.weather.utils.WebServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class MainViewModel @Inject constructor() :ViewModel() {
    val repository  = Repository(ApiManager.getwebbservices())

    suspend fun getLocationDetails(q:Double , q1:Double,units:String) = flow {

        emit(Resource.loading(null))
        try {
            emit(Resource.success(repository.FetchLocationDetails(q,q1,units)))

        } catch (e:Exception){
                emit(Resource.error(null , e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)




    suspend fun getForecastDetails(q:Double , q1:Double , days:Int) = liveData(Dispatchers.IO){
        emit(Resource.loading(null))
        try {
            emit(Resource.success(repository.FetchForecastDetails(q,q1 ,days)))

        } catch (e:Exception){
            emit(Resource.error(null , e.message.toString()))
        }
    }

}