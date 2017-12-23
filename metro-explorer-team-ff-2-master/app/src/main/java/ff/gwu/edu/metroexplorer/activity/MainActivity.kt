package ff.gwu.edu.metroexplorer.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.adapter.LocationDetector
import ff.gwu.edu.metroexplorer.adapter.PersistanceManager
import ff.gwu.edu.metroexplorer.task.YelpAuthAsyncTask
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get yelp token
        val manager = PersistanceManager(this)
        if (manager.fetchYelpToken() == ""){
            val yelpAuthTask = YelpAuthAsyncTask(this)
            yelpAuthTask.fetchAcessToken()
        }

        nearest_button.setOnClickListener {
            println("nearest button touch up")
            val intent = Intent(this, LandmarksActivity::class.java)
            startActivity(intent)
        }

        station_button.setOnClickListener {
            println("station button touch up")
            val intent = Intent(this, MetroStationsActivity::class.java)
            startActivity(intent)
        }

        favorite_button.setOnClickListener {
            println("favorite button touch up")
            val persistanceManger = PersistanceManager(this)
            val landmarks = persistanceManger.fectchFavorites()
            if (landmarks.size>0){
                val intent = Intent(this, FavoriteLandmarksActivity::class.java)
                startActivity(intent)
            }else {
                toast(resources.getString(R.string.nofavorites))
            }
        }
    }
}
