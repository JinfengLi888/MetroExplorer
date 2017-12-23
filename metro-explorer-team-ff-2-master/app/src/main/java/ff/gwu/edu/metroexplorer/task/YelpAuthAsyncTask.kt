package ff.gwu.edu.metroexplorer.task

import android.content.Context
import com.koushikdutta.ion.Ion
import ff.gwu.edu.metroexplorer.adapter.Constant
import ff.gwu.edu.metroexplorer.adapter.PersistanceManager

class YelpAuthAsyncTask(val context: Context) {
    var fetchTokenListener: YelpAuthCompletionListener?= null
    interface YelpAuthCompletionListener{
        fun tokenLoaded(token: String)
        fun tokenNotLoaded()
    }
    fun fetchAcessToken(){
        Ion.with(context).load("POST",Constant.YELP_TOKEN_URL)
                .addQuery("grant_type", Constant.GRANT_TYPE_YELP)
                .addQuery("client_id", Constant.CLIENT_ID_YELP)
                .addQuery("client_secret", Constant.CLIENT_SECRET_YELP)
                .asJsonObject()
                .setCallback { error, result ->
                    error?.let {
                        fetchTokenListener?.tokenNotLoaded()
                    }

                    result?.let {
                        val token = result.get("access_token")
                        if (token != null && token.asString.count() > 0 ){
                            val manager = PersistanceManager(context)
                            manager.saveYelpToken(token.asString)
                            fetchTokenListener?.tokenLoaded(manager.fetchYelpToken())
                        }else{
                            fetchTokenListener?.tokenNotLoaded()
                        }
                    }
                }
    }
}