package com.example.weather.utils

import com.example.weather.ui.MainActivity
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    companion object {
        private var retrofit: Retrofit? = null

        @Synchronized
        private fun getinstance(): Retrofit {
            if (retrofit == null) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                val keyInterceptor = Interceptor{chain :Interceptor.Chain->
                    var original = chain.request()
                    var url: HttpUrl = original.url.newBuilder().addQueryParameter("appid",Constants.API_KEY).build()
                    original = original.newBuilder().url(url).build()
                    chain.proceed(original)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(keyInterceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                return retrofit!!
            }
            return retrofit!!
        }

        fun getwebbservices(): WebServices {
            return getinstance().create(WebServices::class.java)

        }
    }



}