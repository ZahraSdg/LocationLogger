package ir.zahrasdg.locationlogger

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<MainViewModel>(), OnMapReadyCallback, LocationHelper.Listener {

    private lateinit var mMap: GoogleMap

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.title_activity_main)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        (mapFragment as SupportMapFragment).getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()

        viewModel.startLocationUpdates(this)
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        mMap = googleMap

        viewModel.getLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        viewModel.handlePermissionResult(requestCode, grantResults)
        //update ui
    }

    override fun onLocationUpdated(location: Location) {

        viewModel.location = location
    }

    override fun onLocationSettingChanged(successful: Boolean) {
        if (!successful) {
            showLocationSetting()
        }
    }

    override fun onNeedLocationPermission() {
        requestPermission()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    private fun showLocationSetting() {
        val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        this.startActivityForResult(i, AppConstants.REQUEST_CHECK_SETTINGS)
    }
}
