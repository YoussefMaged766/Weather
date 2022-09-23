package com.example.weather.utils

import com.example.weather.models.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WebServices {

    @GET("current.json")
    suspend fun GetLocationDetails(
        @Query("q")  q:Double,
        @Query("q")  q1:Double
    ) :LocationResponse


}
