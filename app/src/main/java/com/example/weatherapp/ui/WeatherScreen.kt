package com.example.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.domain.model.AstronomyInfo
import com.example.weatherapp.domain.model.ForecastDayInfo
import com.example.weatherapp.domain.model.ForecastInfo
import com.example.weatherapp.domain.model.HourlyForecastInfo
import com.example.weatherapp.domain.model.WeatherInfo

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var cityInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B2838),
                        Color(0xFF2C3E50),
                        Color(0xFF1B2838)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Weather App",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Search for a city to get weather info",
                fontSize = 14.sp,
                color = Color(0xFFB0BEC5)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    placeholder = {
                        Text("Enter city name", color = Color(0xFF78909C))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color(0xFF546E7A),
                        cursorColor = Color(0xFF4FC3F7),
                        focusedContainerColor = Color(0xFF263238),
                        unfocusedContainerColor = Color(0xFF263238)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { viewModel.searchWeather(cityInput) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7)
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Search", color = Color(0xFF1B2838), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (val state = uiState) {
                is WeatherUiState.Empty -> {
                    Text(
                        text = "\uD83C\uDF24",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(top = 48.dp)
                    )
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .size(48.dp),
                        color = Color(0xFF4FC3F7)
                    )
                }
                is WeatherUiState.Success -> {
                    WeatherCard(weather = state.weather)
                    state.astronomy?.let { astro ->
                        Spacer(modifier = Modifier.height(16.dp))
                        AstronomyCard(astronomy = astro)
                    }
                    state.forecast?.let { forecast ->
                        Spacer(modifier = Modifier.height(24.dp))
                        ForecastSection(forecast = forecast)
                    }
                }
                is WeatherUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3E2723)
                        )
                    ) {
                        Text(
                            text = state.message,
                            color = Color(0xFFEF9A9A),
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.cityName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${weather.temperature.toInt()}\u00B0C",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4FC3F7)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                color = Color(0xFFB0BEC5)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(label = "Humidity", value = "${weather.humidity}%")
                WeatherDetail(label = "Wind", value = "${weather.windSpeed} m/s")
            }
        }
    }
}

@Composable
fun AstronomyCard(astronomy: AstronomyInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Astronomy",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(label = "☀\uFE0F Sunrise", value = astronomy.sunrise)
                WeatherDetail(label = "\uD83C\uDF05 Sunset", value = astronomy.sunset)
                WeatherDetail(label = "\uD83C\uDF19 Moonrise", value = astronomy.moonrise)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(label = "Moon Phase", value = astronomy.moonPhase)
                WeatherDetail(label = "Illumination", value = "${astronomy.moonIllumination}%")
            }
        }
    }
}

// ── Forecast Section ──

@Composable
fun ForecastSection(forecast: ForecastInfo) {
    Text(
        text = "3-Day Forecast",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    forecast.days.forEach { day ->
        ForecastDayCard(day = day)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ForecastDayCard(day: ForecastDayInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Day header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = day.date,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = day.condition,
                        fontSize = 13.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${day.maxTempC.toInt()}\u00B0 / ${day.minTempC.toInt()}\u00B0",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(label = "\uD83C\uDF27 Rain", value = "${day.chanceOfRain}%")
                WeatherDetail(label = "\uD83D\uDCA7 Humidity", value = "${day.avgHumidity}%")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hourly forecast scrollable row
            Text(
                text = "Hourly",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF78909C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                day.hourly.forEach { hour ->
                    HourlyChip(hour = hour)
                }
            }
        }
    }
}

@Composable
fun HourlyChip(hour: HourlyForecastInfo) {
    // Extract just HH:MM from "2026-03-03 09:00"
    val timeLabel = hour.time.substringAfter(" ")

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF37474F)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeLabel,
                fontSize = 12.sp,
                color = Color(0xFFB0BEC5)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${hour.tempC.toInt()}\u00B0",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = hour.condition,
                fontSize = 10.sp,
                color = Color(0xFF78909C),
                maxLines = 1
            )
        }
    }
}

@Composable
fun WeatherDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF78909C)
        )
    }
}
