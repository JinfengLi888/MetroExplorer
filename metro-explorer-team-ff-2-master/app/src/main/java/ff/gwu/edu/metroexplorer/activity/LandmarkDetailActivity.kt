package ff.gwu.edu.metroexplorer.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.koushikdutta.ion.Ion
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.adapter.PersistanceManager
import ff.gwu.edu.metroexplorer.model.LandmarkModel
import kotlinx.android.synthetic.main.activity_landmark_detail.*
import org.jetbrains.anko.toast

class LandmarkDetailActivity : AppCompatActivity() {

    private lateinit var landmark:LandmarkModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmark_detail)

        landmark = intent.getParcelableExtra<LandmarkModel>(Constant.LANDMARKDETAIL_INTENT)
        textView.text = landmark.title
        var addStr = ""
        landmark.address.forEach {
            addStr += "$it"
        }

        address.text = addStr
        // set navigation back button
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = landmark.title

        // set buttons click event
        share_button.setOnClickListener {
            println("share button ...")
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, landmark.title)
            startActivity(intent)
        }

        favorite_button.setOnClickListener {
            println("favorite button ...")
            val manager = PersistanceManager(this)
            manager.saveFavorites(landmark)
            toast(resources.getString(R.string.saved))
            favorite_button.isEnabled = false
        }

        direct_button.setOnClickListener {
            // call navigation app
            val url = "geo: ${landmark.lat}, ${landmark.lon}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        call_button.setOnClickListener{
            // request call permission
            val fineGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            if (!fineGranted) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CALL_PHONE),
                        Constant.CALL_PHONE_REQUEST_CODE)
            }else{
                callLandmarkPhone()
            }
        }

        // set landmark image
        Ion.with(imageView).load(landmark.imageURL).setCallback { error, result ->
            error?.let {
                toast(resources.getString(R.string.landmarkimageerror))
            }
        }
    }


// make a call to landmark
    @SuppressLint("MissingPermission")
    fun callLandmarkPhone(){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:"+landmark.phone))
        startActivity(intent)
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
        if (requestCode == Constant.CALL_PHONE_REQUEST_CODE){
            if (grantResults.size==1 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
                callLandmarkPhone()
            }else{
                toast(resources.getString(R.string.callerror))
            }
        }
    }
}
