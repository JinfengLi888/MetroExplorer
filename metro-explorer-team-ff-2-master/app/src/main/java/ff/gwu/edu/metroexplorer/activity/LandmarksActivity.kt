package ff.gwu.edu.metroexplorer.activity

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.adapter.LandmarksAdapter
import ff.gwu.edu.metroexplorer.adapter.LocationDetector
import ff.gwu.edu.metroexplorer.model.LandmarkModel
import ff.gwu.edu.metroexplorer.task.FetchLandmarksAsyncTask
import ff.gwu.edu.metroexplorer.task.FetchNearestMetroAsyncTask
import kotlinx.android.synthetic.main.activity_landmarks.*
import org.jetbrains.anko.toast

class LandmarksActivity : AppCompatActivity(),
        FetchLandmarksAsyncTask.SearchYelpCompletionListener,
        LocationDetector.LocationDetectorCompletionListener,
        FetchNearestMetroAsyncTask.FetchNearestMetroCompletionListener {
    override fun locationNotFound() {
        toast(resources.getString(R.string.timeout))
        progressbar.visibility = View.GONE
    }

    override fun locationLoaded(latitude: String, longitude: String) {
        //To change body of created functions use File | Settings | File Templates.
        val fetchLandmarks = FetchLandmarksAsyncTask(this)
        fetchLandmarks.searchYelpCompletionListener = this
        fetchLandmarks.searchYelp(latitude,longitude,"10000")

        // get nearby metro
        val fetchNearestMetro = FetchNearestMetroAsyncTask(this)
        fetchNearestMetro.fetchNearestMetroListener = this
        fetchNearestMetro.fetch(latitude, longitude, "1000")
        progressbar.visibility = View.GONE
    }

    // get permission listener
    override fun permissionDenied() {
        //To change body of created functions use File | Settings | File Templates.
        progressbar.visibility = View.GONE
    }

    override fun GPSNotEnabled() {
        //To change body of created functions use File | Settings | File Templates.
        toast(resources.getString(R.string.enablegps))
        progressbar.visibility = View.GONE
    }

    // landmarks listener
    override fun dataLoaded(landmarks: ArrayList<LandmarkModel>) {
        //To change body of created functions use File | Settings | File Templates.
        landmarkslist.adapter = LandmarksAdapter(this,landmarks)
        progressbar.visibility = View.GONE
    }

    override fun dataNotLoaded() {
        //To change body of created functions use File | Settings | File Templates.
        toast(resources.getString(R.string.serviceerror))
        progressbar.visibility = View.GONE
    }

    override fun dataNotFound() {
        toast(resources.getString(R.string.nolandmarks))
        progressbar.visibility = View.GONE
    }

    // nearest metro listener
    override fun metroFound(metro: String){
        supportActionBar?.title = metro
    }
    override fun metroNotFound(){
        Log.e("metro not found","try again later..")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayout.VERTICAL
        landmarkslist.setHasFixedSize(true)
        landmarkslist.layoutManager = manager
        landmarkslist.addItemDecoration(DividerItemDecoration(
                landmarkslist.context,
                manager.orientation
        ))

        // set navigation back button
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Judge previous activity from nearest button or metro list
        val lat = intent.getStringExtra(Constant.LANDMARKDETAIL_LAT)
        val lon = intent.getStringExtra(Constant.LANDMARKDETAIL_LON)
        val metroName = intent.getStringExtra(Constant.LANDMARKDETAIL_STATIONNAME)
        if (lat != null && lon != null && metroName != null){
            supportActionBar?.title = metroName
            val fetchLandmarks = FetchLandmarksAsyncTask(this)
            fetchLandmarks.searchYelpCompletionListener = this
            fetchLandmarks.searchYelp(lat,lon,"10000")
        }else{
            getUserLocation()
        }
    }

    // get user's location to fetch nearest landmarks
    private fun getUserLocation(){
        val locationDector = LocationDetector(this)
        locationDector.locationDetectorCompletionListener = this
        locationDector.getUsersLocation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.FINE_LOCATION_REQUEST_CODE){
            if (grantResults.size==1 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
                getUserLocation()
            }else{
                toast(resources.getString(R.string.Locationdenied))
                progressbar.visibility = View.GONE
            }
        }
    }
}
