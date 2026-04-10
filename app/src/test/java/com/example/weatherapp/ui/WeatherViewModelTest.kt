package com.example.weatherapp.ui

import app.cash.turbine.test
import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.ForecastDayInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.domain.usecase.GetAstronomyUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var getAstronomyUseCase: GetAstronomyUseCase
    private lateinit var getForecastUseCase: GetForecastUseCase
    private lateinit var viewModel: WeatherViewModel

    private val fakeWeather = WeatherInfo(
        cityName = "Almaty",
        temperature = 15.0,
        description = "Sunny",
        humidity = 40,
        windSpeed = 10.0,
        icon = "https://cdn.weatherapi.com/weather/64x64/day/113.png"
    )

    private val fakeAstronomy = AstronomyInfo(
        sunrise = "06:30 AM",
        sunset = "07:45 PM",
        moonrise = "09:15 PM",
        moonPhase = "Waning Crescent",
        moonIllumination = "25"
    )

    private val fakeForecast = ForecastInfo(
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

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = mock()
        getAstronomyUseCase = mock()
        getForecastUseCase = mock()
        viewModel = WeatherViewModel(getWeatherUseCase, getAstronomyUseCase, getForecastUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Empty`() {
        assertEquals(WeatherUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `searchWeather with blank city does nothing`() = runTest {
        viewModel.searchWeather("   ")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(WeatherUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `searchWeather success flow emits Loading then Success`() = runTest {
        whenever(getWeatherUseCase.invoke(any())).thenReturn(fakeWeather)
        whenever(getAstronomyUseCase.invoke(any(), any())).thenReturn(fakeAstronomy)
        whenever(getForecastUseCase.invoke(any(), any())).thenReturn(fakeForecast)

        viewModel.uiState.test {
            assertEquals(WeatherUiState.Empty, awaitItem())

            viewModel.searchWeather("Almaty")

            assertEquals(WeatherUiState.Loading, awaitItem())

            // Success with weather only
            val successWeather = awaitItem()
            assertTrue(successWeather is WeatherUiState.Success)

            // May get additional emissions as astronomy/forecast load
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchWeather error flow emits Error state`() = runTest {
        whenever(getWeatherUseCase.invoke(any()))
            .thenThrow(RuntimeException("Network error"))

        viewModel.uiState.test {
            assertEquals(WeatherUiState.Empty, awaitItem())

            viewModel.searchWeather("Almaty")

            assertEquals(WeatherUiState.Loading, awaitItem())

            val errorState = awaitItem()
            assertTrue(errorState is WeatherUiState.Error)
            assertEquals("Network error", (errorState as WeatherUiState.Error).message)
        }
    }
}
