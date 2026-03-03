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

data class AstronomyResponse(
    val location: LocationDto,
    val astronomy: AstronomyDataDto
)

data class AstronomyDataDto(
    val astro: AstroDto
)

data class AstroDto(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    @SerializedName("moon_phase")
    val moonPhase: String,
    @SerializedName("moon_illumination")
    val moonIllumination: String
)

// ── Forecast DTOs ──

data class ForecastResponse(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto
)

data class ForecastDto(
    val forecastday: List<ForecastDayDto>
)

data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val hour: List<HourDto>
)

data class DayDto(
    @SerializedName("maxtemp_c")
    val maxTempC: Double,
    @SerializedName("mintemp_c")
    val minTempC: Double,
    @SerializedName("avgtemp_c")
    val avgTempC: Double,
    @SerializedName("maxwind_kph")
    val maxWindKph: Double,
    @SerializedName("totalprecip_mm")
    val totalPrecipMm: Double,
    @SerializedName("avghumidity")
    val avgHumidity: Int,
    @SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int,
    val condition: ConditionDto,
    val uv: Double
)

data class HourDto(
    val time: String,
    @SerializedName("temp_c")
    val tempC: Double,
    val condition: ConditionDto,
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("chance_of_rain")
    val chanceOfRain: Int,
    @SerializedName("feelslike_c")
    val feelsLikeC: Double
)
