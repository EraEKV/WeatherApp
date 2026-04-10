package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.ForecastDayInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetForecastUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetForecastUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetForecastUseCase(repository)
    }

    @Test
    fun `invoke returns ForecastInfo with default 3 days`() = runTest {
        // Given
        val expected = ForecastInfo(
            days = listOf(
                ForecastDayInfo(
                    date = "2026-04-10",
                    maxTempC = 20.0,
                    minTempC = 10.0,
                    avgTempC = 15.0,
                    condition = "Sunny",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
                    chanceOfRain = 0,
                    avgHumidity = 40,
                    hourly = emptyList()
                )
            )
        )
        whenever(repository.getForecast("Almaty", 3)).thenReturn(expected)

        // When
        val result = useCase("Almaty")

        // Then
        assertEquals(expected, result)
        assertEquals(1, result.days.size)
        verify(repository).getForecast("Almaty", 3)
    }

    @Test
    fun `invoke passes custom days parameter`() = runTest {
        // Given
        val expected = ForecastInfo(days = emptyList())
        whenever(repository.getForecast("Astana", 7)).thenReturn(expected)

        // When
        val result = useCase("Astana", 7)

        // Then
        assertEquals(expected, result)
        verify(repository).getForecast("Astana", 7)
    }
}
