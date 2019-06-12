package ir.zahrasdg.locationlogger

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.location.LocationServices
import ir.zahrasdg.locationlogger.model.LocationLoggerDataBase
import ir.zahrasdg.locationlogger.repo.LocationRepository
import ir.zahrasdg.locationlogger.repo.UserStatusRepository
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

class App : Application() {

    private val appModule = module {
        factory { UserStatusRepository(LocationLoggerDataBase.getDatabase(androidContext()).userStatusDao()) }
        factory { LocationRepository(LocationServices.getFusedLocationProviderClient(androidContext())) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}