package ir.zahrasdg.locationlogger

import android.app.Application
import android.support.v7.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}