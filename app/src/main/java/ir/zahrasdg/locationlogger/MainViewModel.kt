package ir.zahrasdg.locationlogger

import android.app.Application
import android.location.Location
import org.koin.standalone.inject

class MainViewModel(application: Application) : BaseAndroidViewModel(application) {

    var location = Location("")
    private val locationHelper by inject<LocationHelper>()

    fun startLocationUpdates(listener: LocationHelper.Listener) {
        locationHelper.listener = listener
        locationHelper.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationHelper.stopLocationUpdates()
        locationHelper.listener = null
    }

    fun getLocationPermission() {
        locationHelper.getLocationPermission()
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        locationHelper.handlePermissionResult(requestCode, grantResults)
    }
}