package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.ForecastDayInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.model.HourlyForecastInfo
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

    override suspend fun getForecast(city: String, days: Int): ForecastInfo {
        val response = api.getForecast(city = city, days = days, apiKey = BuildConfig.WEATHER_API_KEY)
        return ForecastInfo(
            days = response.forecast.forecastday.map { day ->
                ForecastDayInfo(
                    date = day.date,
                    maxTempC = day.day.maxTempC,
                    minTempC = day.day.minTempC,
                    avgTempC = day.day.avgTempC,
                    condition = day.day.condition.text,
                    icon = "https:" + day.day.condition.icon,
                    chanceOfRain = day.day.dailyChanceOfRain,
                    avgHumidity = day.day.avgHumidity,
                    hourly = day.hour.map { hour ->
                        HourlyForecastInfo(
                            time = hour.time,
                            tempC = hour.tempC,
                            condition = hour.condition.text,
                            icon = "https:" + hour.condition.icon,
                            humidity = hour.humidity,
                            windKph = hour.windKph,
                            chanceOfRain = hour.chanceOfRain
                        )
                    }
                )
            }
        )
    }
}

