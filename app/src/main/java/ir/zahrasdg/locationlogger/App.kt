package ir.zahrasdg.locationlogger

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ir.zahrasdg.locationlogger.model.UserStatusRoomDataBase
import ir.zahrasdg.locationlogger.repo.UserStatusRepository
import ir.zahrasdg.locationlogger.util.LocationHelper
import org.koin.android.ext.android.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

class App : Application() {

    private val appModule = module {
        factory { LocationHelper(androidContext()) }
        factory { UserStatusRepository(UserStatusRoomDataBase.getDatabase(androidContext()).userStatusDao()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}