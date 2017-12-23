package ff.gwu.edu.metroexplorer.task

import android.content.Context
import android.util.Log
import com.koushikdutta.ion.Ion
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.model.MetroModel

/**
 * Created by Jinfeng on 10/4/17.
 */
class FetchNearestMetroAsyncTask(val context: Context) {

    var fetchNearestMetroListener: FetchNearestMetroCompletionListener ?= null

    interface FetchNearestMetroCompletionListener {
        fun metroFound(metro: String)
        fun metroNotFound()
    }
    fun fetch(lantitude: String, longitude: String, radius: String){
        Ion.with(context).load(Constant.FETCH_NEARBY_ENTRANCES_URL)
                .addHeader("api_key", Constant.API_KEY_METRO)
                .addQuery("Lat", lantitude)
                .addQuery("Lon", longitude)
                .addQuery("Radius", radius)
                .asJsonObject()
                .setCallback { error, result ->
                    error?.let {
                        fetchNearestMetroListener?.metroNotFound()
                    }
                    result?.let {
                        val entrances = result.asJsonObject.get("Entrances")
                        if (entrances != null && entrances.asJsonArray.count() > 0){
                            val entrancesArray = entrances.asJsonArray
                            val item = entrancesArray[0].asJsonObject
                            val stationCode = item.get("StationCode1").asString
                            if (stationCode != null && stationCode.toCharArray().isNotEmpty()){
                                Log.e("nearest metro code is :",stationCode)
                                Ion.with(context).load(Constant.FETCH_STATION_INFO_URL)
                                        .addHeader("api_key", Constant.API_KEY_METRO)
                                        .addQuery("StationCode", stationCode)
                                        .asJsonObject()
                                        .setCallback { error, result ->
                                            error?.let {
                                                fetchNearestMetroListener?.metroNotFound()
                                            }
                                            result?.let {
                                                val metroInfo = result.asJsonObject
                                                if (metroInfo != null && metroInfo.size() > 0){
                                                    val metro = result.asJsonObject.get("Name")
                                                    fetchNearestMetroListener?.metroFound(metro.asString)
                                                }else{
                                                    fetchNearestMetroListener?.metroNotFound()
                                                }
                                            }
                                        }
                            }else{
                                fetchNearestMetroListener?.metroNotFound()
                            }
                        }else{
                            fetchNearestMetroListener?.metroNotFound()
                        }
                    }
                }

    }
}