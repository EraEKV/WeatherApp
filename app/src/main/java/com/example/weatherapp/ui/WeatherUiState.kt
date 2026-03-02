package com.example.weatherapp.ui

import com.example.weatherapp.domain.model.WeatherInfo

sealed class WeatherUiState {
    object Empty : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherInfo) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
