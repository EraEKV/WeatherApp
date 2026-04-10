package com.example.weatherapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.GetAstronomyUseCase
import com.example.weatherapp.domain.usecase.GetForecastUseCase
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import com.example.weatherapp.ui.theme.WeatherAppTheme
import org.junit.Rule
import org.junit.Test

/**
 * Fake repository that returns predefined data without network calls.
 */
class FakeWeatherRepository : WeatherRepository {
    override suspend fun getWeather(city: String): WeatherInfo {
        return WeatherInfo(
            cityName = city,
            temperature = 15.0,
            description = "Sunny",
            humidity = 40,
            windSpeed = 10.0,
            icon = "https://cdn.weatherapi.com/weather/64x64/day/113.png"
        )
    }

    override suspend fun getAstronomy(city: String, date: String): AstronomyInfo {
        throw RuntimeException("Not needed for this test")
    }

    override suspend fun getForecast(city: String, days: Int): ForecastInfo {
        throw RuntimeException("Not needed for this test")
    }
}

class WeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): WeatherViewModel {
        val fakeRepo = FakeWeatherRepository()
        return WeatherViewModel(
            getWeatherUseCase = GetWeatherUseCase(fakeRepo),
            getAstronomyUseCase = GetAstronomyUseCase(fakeRepo),
            getForecastUseCase = GetForecastUseCase(fakeRepo)
        )
    }

    @Test
    fun searchField_and_button_are_displayed() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Weather App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter city name").assertIsDisplayed()
    }

    @Test
    fun typing_city_and_clicking_search_shows_weather() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Enter city name").performTextInput("Almaty")
        composeTestRule.onNodeWithText("Search").performClick()

        // Wait for the weather card to appear
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodes(hasText("Almaty"))
                .fetchSemanticsNodes()
                .size > 1  // title + weather card
        }

        composeTestRule.onNodeWithText("Sunny").assertIsDisplayed()
        composeTestRule.onNodeWithText("40%").assertIsDisplayed()
    }
}
