package ir.zahrasdg.locationlogger.repo

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.*

class LocationRepository(private val fusedLocationClient: FusedLocationProviderClient) {

    companion object {

        private val TAG = LocationRepository::class.java.name
        private const val TIME_INTERVAL = 5000L
        private const val DISPLACEMENT = 5F
    }


    //region Private parameters
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback
    //endregion

    //region Public parameters
    val location = MutableLiveData<Location>()
    var locationSettingRequest: LocationSettingsRequest? = null
    //endregion

    init {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult$location")
                    Log.d(TAG, "onLocationTime" + Calendar.getInstance().timeInMillis / 1000)

                    this@LocationRepository.location.postValue(location)
                }
            }
        }

        locationRequest = LocationRequest.create()?.apply {
            interval = TIME_INTERVAL
            fastestInterval = TIME_INTERVAL / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = DISPLACEMENT
        }

        locationSettingRequest = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }?.build()
    }

    //region Public functions
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener

                Log.d(TAG, "lastKnownLocation$location")
                this@LocationRepository.location.postValue(location)
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    //endregion
}