package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.AppComponent
import com.example.weatherapp.di.DaggerAppComponent

class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }
}
