package ir.zahrasdg.locationlogger.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*

class LocationHelper(private val context: Context) {

    var listener: Listener? = null
    private var locationPermissionGranted = false
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback


    init {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {

                    Log.d(TAG, "onLocationResult$location")
                    Log.d(TAG, "onLocationTime" + Calendar.getInstance().timeInMillis / 1000)
                    listener?.onLocationUpdated(location)
                }
            }
        }

        createLocationRequest()
    }

    fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                context.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            getLastKnownLocation()
        } else {
            listener?.onNeedLocationPermission()
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = TIME_INTERVAL
            fastestInterval = TIME_INTERVAL / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = DISPLACEMENT
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.

            Log.d(TAG, "All location settings are satisfied")
            listener?.onLocationSettingChanged(true)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d(TAG, "Location settings are not satisfied")
                listener?.onLocationSettingChanged(false)
            }
        }
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    getLastKnownLocation()
                }
            }
        }
        //update ui
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener

                Log.d(TAG, "lastKnownLocation$location")
                listener?.onLocationUpdated(location)
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    interface Listener {

        fun onLocationUpdated(location: Location)
        fun onLocationSettingChanged(successful: Boolean)
        fun onNeedLocationPermission()
    }

    companion object {
        private val TAG = LocationHelper::class.java.name
        private const val TIME_INTERVAL = 5000L
        private const val DISPLACEMENT = 2F
    }
}