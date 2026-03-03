package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.usecase.GetAstronomyUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getAstronomyUseCase: GetAstronomyUseCase,
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun searchWeather(city: String) {
        if (city.isBlank()) return
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            try {
                val weather = getWeatherUseCase(city)
                _uiState.value = WeatherUiState.Success(weather = weather)

                // Fetch astronomy data with today's date
                var astronomy: com.example.weatherapp.domain.model.AstronomyInfo? = null
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                try {
                    astronomy = getAstronomyUseCase(city, today)
                    _uiState.value = WeatherUiState.Success(
                        weather = weather,
                        astronomy = astronomy
                    )
                } catch (_: Exception) {
                    // Astronomy is optional — weather card stays visible
                }

                // Fetch 3-day forecast
                try {
                    val forecast = getForecastUseCase(city)
                    _uiState.value = WeatherUiState.Success(
                        weather = weather,
                        astronomy = astronomy,
                        forecast = forecast
                    )
                } catch (_: Exception) {
                    // Forecast is optional — weather + astronomy stay visible
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }
}

