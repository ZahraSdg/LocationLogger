package ir.zahrasdg.locationlogger.view

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ir.zahrasdg.locationlogger.R
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.util.AppConstants
import ir.zahrasdg.locationlogger.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity<MainViewModel>(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 300
    }

    private lateinit var map: GoogleMap

    //region Overridden functions
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.title_activity_main)

        (mapFragment as SupportMapFragment).getMapAsync(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.location.observe(this, Observer { location ->
            location?.let {
                viewModel.logStatus(UserStatus(0, it.latitude, it.longitude, Calendar.getInstance().timeInMillis))
            }
        })

        viewModel.userStatuses.observe(this, Observer { statuses ->
            statuses?.let {
                if (it.isNotEmpty()) {
                    addMarkers(it)
                    viewModel.incrementPage()
                } else { // end of paging saved data
                    viewModel.isOnColdStart = false
                    viewModel.userStatuses.removeObservers(this)
                }
            }

        })

        viewModel.newlyInsertedStatus.observe(this, Observer<UserStatus> { userStatus ->
            userStatus?.let {
                addMarker(it)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f),
                    1500,
                    null
                )
            }
        })

        viewModel.locationSettingSatisfied.observe(this, Observer { satisfied ->
            satisfied?.let {
                if (!it) {
                    showLocationSetting()
                }
            }
        })

        viewModel.locationPermissionGranted.observe(this, Observer { permissionGranted ->
            permissionGranted?.let {
                if (it){
                    viewModel.getLastKnownLocation()
                } else {
                    requestPermission()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkLocationSettings()
        viewModel.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        map = googleMap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        viewModel.handlePermissionResult(requestCode, grantResults)
    }
    //endregion

    //region Private functions
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            AppConstants.LOCATION_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun showLocationSetting() {
        val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        this.startActivityForResult(i, REQUEST_CHECK_SETTINGS)
    }

    private fun addMarkers(statuses: List<UserStatus>) {
        statuses.forEach {
            addMarker(it)
        }
    }

    private fun addMarker(status: UserStatus) {
        map.addMarker(
            MarkerOptions()
                .title(status.id.toString())
                .snippet(viewModel.getDateCurrentTimeZone(status.timeStamp))
                .icon(getBitmapDescriptor(R.drawable.ic_dot))
                .position(LatLng(status.latitude, status.longitude))
        )
    }

    private fun getBitmapDescriptor(id: Int): BitmapDescriptor {
        val vectorDrawable = if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat.create(resources, id, theme)
        } else {
            resources.getDrawable(id, theme)
        }
        val bitmap = vectorDrawable?.intrinsicWidth?.let {
            Bitmap.createBitmap(
                it,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap!!)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    //endregion
}
