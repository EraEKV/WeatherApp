package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetAstronomyUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetAstronomyUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetAstronomyUseCase(repository)
    }

    @Test
    fun `invoke returns AstronomyInfo from repository`() = runTest {
        // Given
        val expected = AstronomyInfo(
            sunrise = "06:30 AM",
            sunset = "07:45 PM",
            moonrise = "09:15 PM",
            moonPhase = "Waning Crescent",
            moonIllumination = "25"
        )
        whenever(repository.getAstronomy("Almaty", "2026-04-10")).thenReturn(expected)

        // When
        val result = useCase("Almaty", "2026-04-10")

        // Then
        assertEquals(expected, result)
        assertEquals("06:30 AM", result.sunrise)
        assertEquals("07:45 PM", result.sunset)
        verify(repository).getAstronomy("Almaty", "2026-04-10")
    }

    @Test(expected = Exception::class)
    fun `invoke throws when repository throws`() = runTest {
        // Given
        whenever(repository.getAstronomy("Unknown", "2026-04-10"))
            .thenThrow(RuntimeException("API error"))

        // When
        useCase("Unknown", "2026-04-10")
    }
}
