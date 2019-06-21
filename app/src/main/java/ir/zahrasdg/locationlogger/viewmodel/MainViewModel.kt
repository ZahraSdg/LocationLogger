package ir.zahrasdg.locationlogger.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.repo.LocationRepository
import ir.zahrasdg.locationlogger.repo.UserStatusRepository
import ir.zahrasdg.locationlogger.util.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.standalone.inject
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(application: Application) : BaseAndroidViewModel(application) {

    companion object {

        private const val MIN_DISPLACEMENT = 5F
    }

    //region Private parameters
    private val userStatusRepository by inject<UserStatusRepository>()
    private val locationRepository by inject<LocationRepository>()
    private var pageNumber = MutableLiveData<Int>()
    private var newlyInsertedId = MutableLiveData<Int>()
    private var lastLocation: Location? = null
    //endregion

    //region Public parameters
    val location = locationRepository.location
    var locationPermissionGranted = MutableLiveData<Boolean>()
    var locationSettingSatisfied = MutableLiveData<Boolean>()
    var isOnColdStart = false
    var userStatuses: LiveData<List<UserStatus>> = Transformations.switchMap(pageNumber, ::loadNextPage)
    var newlyInsertedStatus: LiveData<UserStatus> =
        Transformations.switchMap(newlyInsertedId, ::loadSingleStatus)
    //endregion

    init {
        isOnColdStart = true
        pageNumber.value = 1
        checkLocationPermission()
    }

    //region Private functions
    private fun checkLocationPermission() {

        locationPermissionGranted.value = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadSingleStatus(id: Int) = userStatusRepository.loadNewlyInsertedStatus(id)

    private fun loadNextPage(pageNum: Int) = userStatusRepository.loadStatusPage(pageNum)

    private fun insertStatus(userStatus: UserStatus) = viewModelScope.launch(Dispatchers.IO) {
        val insertedId = userStatusRepository.insert(userStatus).toInt()
        if (!isOnColdStart) {
            newlyInsertedId.postValue(insertedId)
        }
    }
    //endregion

    //region Public functions
    fun logStatus(userStatus: UserStatus) {
        location.value?.let {
            if (lastLocation == null || (it.distanceTo(lastLocation) > MIN_DISPLACEMENT)) {
                insertStatus(userStatus)
            }
            lastLocation = location.value
        }
    }

    fun incrementPage() {
        pageNumber.postValue(pageNumber.value?.inc())
    }

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        locationRepository.stopLocationUpdates()
    }

    fun getLastKnownLocation() {
        locationRepository.getLastKnownLocation()
    }

    fun checkLocationSettings() {

        val client: SettingsClient = LocationServices.getSettingsClient(getApplication<Application>())
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(locationRepository.locationSettingRequest)

        task.addOnSuccessListener {
            locationSettingSatisfied.postValue(true)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                locationSettingSatisfied.postValue(false)
            }
        }
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.LOCATION_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted.value = true
                    getLastKnownLocation()
                } else {
                    locationPermissionGranted.value = false
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun getDateCurrentTimeZone(timestamp: Long): String {
        try {
            val calendar = Calendar.getInstance()
            val tz = TimeZone.getDefault()
            calendar.timeInMillis = timestamp
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getApplication<Application>().resources.configuration.locales[0]
            } else {
                getApplication<Application>().resources.configuration.locale
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
            val currentTimeZone = calendar.time as Date
            return sdf.format(currentTimeZone)
        } catch (e: Exception) {
        }
        return ""
    }
    //endregion
}