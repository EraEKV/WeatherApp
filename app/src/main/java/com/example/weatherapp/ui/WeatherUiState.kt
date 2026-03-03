package com.example.weatherapp.ui

import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.model.WeatherInfo

sealed class WeatherUiState {
    object Empty : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(
        val weather: WeatherInfo,
        val astronomy: AstronomyInfo? = null,
        val forecast: ForecastInfo? = null
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

