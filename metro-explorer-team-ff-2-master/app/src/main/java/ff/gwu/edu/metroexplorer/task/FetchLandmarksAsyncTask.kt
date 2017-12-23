package ff.gwu.edu.metroexplorer.task

import android.content.Context
import com.koushikdutta.ion.Ion
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.adapter.PersistanceManager
import ff.gwu.edu.metroexplorer.model.LandmarkModel

class FetchLandmarksAsyncTask(val context: Context):YelpAuthAsyncTask.YelpAuthCompletionListener {

    private lateinit var latitudee: String
    private lateinit var longitudee: String
    private lateinit var radiuss: String

    var searchYelpCompletionListener: SearchYelpCompletionListener ?= null
    interface SearchYelpCompletionListener{
        fun dataLoaded(landmarks:ArrayList<LandmarkModel>)
        fun dataNotLoaded() // services error
        fun dataNotFound()  // no more landmarks around the location
    }

    fun searchYelp(latitude: String, longitude: String, radius: String){
        val manager = PersistanceManager(context)
        val token = manager.fetchYelpToken()
        if (token == ""){
            latitudee = latitude
            longitudee = longitude
            radiuss = radius

            val yelpAuth = YelpAuthAsyncTask(context)
            yelpAuth.fetchTokenListener = this
            yelpAuth.fetchAcessToken()
        }else{
            getDataFromYelp(latitude, longitude, radius, token)
        }
    }

    override fun tokenNotLoaded() {
        getDataFromYelp(latitudee, longitudee, radiuss, Constant.ACCESS_TOKEN_YELP)
    }

    override fun tokenLoaded(token: String) {
        getDataFromYelp(latitudee, longitudee, radiuss, token)
    }

    private fun getDataFromYelp(latitude: String, longitude: String, radius: String, token: String){
        Ion.with(context).load(Constant.YELP_SEARCH_URL)
                .addHeader("Authorization", token)
                .addQuery("term","landmarks")
                .addQuery("latitude", latitude)
                .addQuery("longitude", longitude)
                .addQuery("radius", radius)
                .asJsonObject()
                .setCallback { error, result ->
                    error?.let {
                        searchYelpCompletionListener?.dataNotLoaded()
                    }
                    result?.let {
                        val business = result.get("businesses")
                        if(business != null){
                            if( business.asJsonArray.size() > 0){
                                val businessArray = business.asJsonArray
                                val landmarksArray = ArrayList<LandmarkModel>()
                                businessArray.forEach {
                                    val item = it.asJsonObject
                                    val locationArray = item.get("location").asJsonObject.get("display_address").asJsonArray
                                    val locationList = ArrayList<String>()
                                    locationArray.forEach{
                                        locationList.add(it.asString)
                                    }
                                    val landmark = LandmarkModel(item.get("id").asString,
                                            item.get("name").asString,
                                            item.get("image_url").asString,
                                            locationList,
                                            item.get("coordinates").asJsonObject.get("latitude").asString,
                                            item.get("coordinates").asJsonObject.get("longitude").asString,
                                            item.get("phone").asString)
                                    landmarksArray.add(landmark)
                                }
                                searchYelpCompletionListener?.dataLoaded(landmarksArray)
                            }else{
                                searchYelpCompletionListener?.dataNotFound()
                            }
                        }else{
                            searchYelpCompletionListener?.dataNotLoaded()
                        }
                    }
                }
    }
}