package com.example.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("key") apiKey: String
    ): WeatherResponse

    @GET("astronomy.json")
    suspend fun getAstronomy(
        @Query("q") city: String,
        @Query("dt") date: String,
        @Query("key") apiKey: String
    ): AstronomyResponse
}
