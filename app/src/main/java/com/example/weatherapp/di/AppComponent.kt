package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.ui.WeatherViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, RepositoryModule::class])
interface AppComponent {
    fun weatherViewModel(): WeatherViewModel

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
