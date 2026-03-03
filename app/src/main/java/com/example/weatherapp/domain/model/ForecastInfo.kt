package com.example.weatherapp.domain.model

data class ForecastInfo(
    val days: List<ForecastDayInfo>
)

data class ForecastDayInfo(
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val avgTempC: Double,
    val condition: String,
    val icon: String,
    val chanceOfRain: Int,
    val avgHumidity: Int,
    val hourly: List<HourlyForecastInfo>
)

data class HourlyForecastInfo(
    val time: String,
    val tempC: Double,
    val condition: String,
    val icon: String,
    val humidity: Int,
    val windKph: Double,
    val chanceOfRain: Int
)
