package com.example.weather.utils

import com.example.weather.models.*
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServices {

    @GET("onecall?")
    suspend fun GetLocationDetails(
        @Query("lat")  q:Double,
        @Query("lon")  q1:Double,
        @Query("units") units:String
    ) :CurrentResponse




}
