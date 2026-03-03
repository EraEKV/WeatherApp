package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getWeather(city: String): WeatherInfo
    suspend fun getAstronomy(city: String, date: String): AstronomyInfo
}
