package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(city: String): WeatherInfo {
        val response = api.getWeather(city = city, apiKey = BuildConfig.WEATHER_API_KEY)
        return WeatherInfo(
            cityName = response.location.name,
            temperature = response.current.tempC,
            description = response.current.condition.text,
            humidity = response.current.humidity,
            windSpeed = response.current.windKph,
            icon = "https:" + response.current.condition.icon
        )
    }

    override suspend fun getAstronomy(city: String, date: String): AstronomyInfo {
        val response = api.getAstronomy(city = city, date = date, apiKey = BuildConfig.WEATHER_API_KEY)
        val astro = response.astronomy.astro
        return AstronomyInfo(
            sunrise = astro.sunrise,
            sunset = astro.sunset,
            moonrise = astro.moonrise,
            moonPhase = astro.moonPhase,
            moonIllumination = astro.moonIllumination
        )
    }
}
