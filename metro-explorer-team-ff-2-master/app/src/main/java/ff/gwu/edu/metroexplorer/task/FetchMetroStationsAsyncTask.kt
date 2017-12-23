package ff.gwu.edu.metroexplorer.task

import android.content.Context
import android.util.Log
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.model.MetroModel
import java.util.*
import kotlin.collections.ArrayList

class FetchMetroStationsAsyncTask (val context: Context) {
    private val TAG = "FetchMetroStationsAsyncTask"
    var fetchMetroCompletionListener: FetchMetroCompletionListener?= null

    interface FetchMetroCompletionListener {
        fun metroLoaded(metros: ArrayList<MetroModel>)
        fun metroNotLoaded()
    }

    fun fetch() {
        Ion.with(context).load(Constant.FETCH_METRO_URL)
                .addHeader("api_key",Constant.API_KEY_METRO)
                .asJsonObject()
                .setCallback(FutureCallback { error, result ->
                    error?.let {
                        
                        //Fail - Network request to METRO failed
                        fetchMetroCompletionListener?.metroNotLoaded()
                    }

                    result?.let {

                        val stations = result.asJsonObject.get("Stations")
                        if (stations != null && stations.asJsonArray.size() > 0) {
                            val stationsArray = stations.asJsonArray
                            val metroArray = ArrayList<MetroModel>()
                            stationsArray.forEach {
                                val item = it.asJsonObject
                                val metro = MetroModel(item.get("Code").asString,
                                        item.get("Name").asString,
                                        item.get("Lat").asString,
                                        item.get("Lon").asString)
                                metroArray.add(metro)
                            }
                            fetchMetroCompletionListener?.metroLoaded(metroArray)
                        }else {
                            fetchMetroCompletionListener?.metroNotLoaded()
                        }


                    }
                })
    }
}

