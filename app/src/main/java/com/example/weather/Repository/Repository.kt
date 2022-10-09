package com.example.weather.Repository

import com.example.weather.utils.WebServices

class Repository(val webServices: WebServices) {

    suspend fun FetchLocationDetails(q:Double , q1:Double , units:String) = webServices.GetLocationDetails(q,q1,units)


}