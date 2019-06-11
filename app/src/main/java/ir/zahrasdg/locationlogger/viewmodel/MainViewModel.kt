package ir.zahrasdg.locationlogger.viewmodel

import android.app.Application
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.LiveData
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.repo.UserStatusRepository
import ir.zahrasdg.locationlogger.util.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.standalone.inject

class MainViewModel(application: Application) : BaseAndroidViewModel(application) {


    var location: Location
    private val locationHelper by inject<LocationHelper>()
    private val repository by inject<UserStatusRepository>()
    val userStatuses: LiveData<List<UserStatus>>

    init {
        userStatuses = repository.allWords
        location = Location(LocationManager.GPS_PROVIDER)
    }

    fun logStatus(userStatus: UserStatus) {
        val newLocation = Location(LocationManager.GPS_PROVIDER)
        newLocation.latitude = userStatus.latitude
        newLocation.longitude = userStatus.longitude

        if (location.distanceTo(newLocation) < 5f)
            return

        insertStatus(userStatus)
    }

    private fun insertStatus(userStatus: UserStatus) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(userStatus)
    }

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