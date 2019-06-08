package ir.zahrasdg.locationlogger

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

class App : Application() {

    private val appModule = module {
        factory { LocationHelper(androidContext()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}