package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String, days: Int = 3): ForecastInfo {
        return repository.getForecast(city, days)
    }
}
