package ir.zahrasdg.locationlogger

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>(), OnMapReadyCallback {

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

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return

        mMap = googleMap
    }
}
