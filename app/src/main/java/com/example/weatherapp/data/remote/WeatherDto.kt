package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: LocationDto,
    val current: CurrentDto
)

data class LocationDto(
    val name: String,
    val region: String,
    val country: String
)

data class CurrentDto(
    @SerializedName("temp_c")
    val tempC: Double,
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("feelslike_c")
    val feelsLikeC: Double,
    val condition: ConditionDto
)

data class ConditionDto(
    val text: String,
    val icon: String
)
