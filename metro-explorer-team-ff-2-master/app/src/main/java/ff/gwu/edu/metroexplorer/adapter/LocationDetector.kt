package ff.gwu.edu.metroexplorer.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import java.util.*
import kotlin.concurrent.timerTask

class LocationDetector(contxt:Context): AppCompatActivity() {


    private var isGPSEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false
    private val timer = Timer()

    private val currentContxt:Context = contxt
    private var locationManager:LocationManager = currentContxt.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val fusedLocationClient: FusedLocationProviderClient = FusedLocationProviderClient(currentContxt)

    var locationDetectorCompletionListener:LocationDetectorCompletionListener ?= null

    interface LocationDetectorCompletionListener {
        fun locationLoaded(latitude: String, longitude: String)
        fun permissionDenied()
        fun GPSNotEnabled()
        fun locationNotFound()
    }

    fun getUsersLocation() {
        //check permission if no request permission
        val fineGranted = ContextCompat.checkSelfPermission(currentContxt, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted){
            ActivityCompat.requestPermissions(
                    currentContxt as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constant.FINE_LOCATION_REQUEST_CODE)
        }else{
            // start setting location manager
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled){
                locationDetectorCompletionListener?.GPSNotEnabled()
            }else{
                // can get users location
                startUpdateLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startUpdateLocation(){
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                timer.cancel()
                locationDetectorCompletionListener?.locationLoaded(
                        locationResult.locations.first().latitude.toString(),
                        locationResult.locations.first().longitude.toString())
            }
        }

        timer.schedule(timerTask {
            runOnUiThread {
//                val location = fusedLocationClient.lastLocation.fi
//                if(location != null){
//                    locationDetectorCompletionListener?.locationLoaded(location.latitude.toString(), location.longitude.toString())
//                }else{
                locationDetectorCompletionListener?.locationNotFound()
                fusedLocationClient.removeLocationUpdates(locationCallback)
//                }
            }
        }, 10*1000)

        val locationRequest = LocationRequest()
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }
}