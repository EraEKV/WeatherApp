package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `invoke returns WeatherInfo from repository`() = runTest {
        // Given
        val expected = WeatherInfo(
            cityName = "Almaty",
            temperature = 15.0,
            description = "Sunny",
            humidity = 40,
            windSpeed = 10.0,
            icon = "https://cdn.weatherapi.com/weather/64x64/day/113.png"
        )
        whenever(repository.getWeather("Almaty")).thenReturn(expected)

        // When
        val result = useCase("Almaty")

        // Then
        assertEquals(expected, result)
        verify(repository).getWeather("Almaty")
    }

    @Test(expected = Exception::class)
    fun `invoke throws when repository throws`() = runTest {
        // Given
        whenever(repository.getWeather("Unknown"))
            .thenThrow(RuntimeException("City not found"))

        // When
        useCase("Unknown")
    }
}
