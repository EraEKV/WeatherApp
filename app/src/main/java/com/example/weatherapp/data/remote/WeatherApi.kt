package com.example.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("key") apiKey: String
    ): WeatherResponse
}
