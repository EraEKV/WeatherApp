package com.example.weatherapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.weatherapp.di.AppComponent
import com.example.weatherapp.di.DaggerAppComponent

class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent

    /**
     * INTENTIONAL LEAK for LeakCanary demonstration.
     * This holds a reference to a destroyed Activity, preventing GC.
     * Remove after demo!
     */
    var leakedActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)

        // Register lifecycle callback to intentionally leak the Activity
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // Store reference — this causes a leak when Activity is destroyed
                leakedActivity = activity
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                // Intentionally NOT clearing the reference — this is the leak!
            }
        })
    }
}
