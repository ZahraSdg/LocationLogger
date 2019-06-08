package ir.zahrasdg.locationlogger

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.koin.standalone.KoinComponent

abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
}