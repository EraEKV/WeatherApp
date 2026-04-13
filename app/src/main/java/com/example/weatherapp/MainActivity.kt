package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.di.ViewModelFactory
import com.example.weatherapp.ui.WeatherScreen
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import leakcanary.AppWatcher

class MainActivity : ComponentActivity() {

    companion object {
        /**
         * INTENTIONAL MEMORY LEAK for LeakCanary demo.
         * Static list keeps strong references to objects that
         * LeakCanary expects to be garbage collected.
         * Remove after demo!
         */
        val leakedObjects = mutableListOf<Any>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission for Chucker (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        val appComponent = (application as WeatherApplication).appComponent
        val viewModel = ViewModelProvider(
            this,
            ViewModelFactory { appComponent.weatherViewModel() }
        )[WeatherViewModel::class.java]

        setContent {
            WeatherAppTheme {
                WeatherScreen(viewModel = viewModel)
            }
        }

        // Trigger intentional leak after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            triggerIntentionalLeak()
        }, 3000)
    }

    /**
     * Creates an object, tells LeakCanary to watch it (expecting GC),
     * but keeps a static reference so GC can't collect it → LEAK.
     */
    private fun triggerIntentionalLeak() {
        val leakyObject = object : Any() {
            override fun toString() = "IntentionalLeakObject"
        }

        // Tell LeakCanary: "this object should be GC'd soon"
        AppWatcher.objectWatcher.expectWeaklyReachable(
            leakyObject,
            "Intentional leak for demo"
        )

        // But keep a strong static reference → GC can't collect it → LEAK detected!
        leakedObjects.add(leakyObject)
    }
}