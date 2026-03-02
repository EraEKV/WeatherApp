package com.example.weatherapp.di

import com.example.weatherapp.ui.WeatherViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun weatherViewModel(): WeatherViewModel
}
