package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetAstronomyUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String, date: String): AstronomyInfo {
        return repository.getAstronomy(city, date)
    }
}
