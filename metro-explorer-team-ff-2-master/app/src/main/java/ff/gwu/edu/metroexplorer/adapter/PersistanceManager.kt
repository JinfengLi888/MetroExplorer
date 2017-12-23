package ff.gwu.edu.metroexplorer.adapter

import android.content.Context
import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ff.gwu.edu.metroexplorer.model.LandmarkModel

class PersistanceManager(context:Context) {
    var sharedPreferences:SharedPreferences

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveFavorites(landmark: LandmarkModel){
        val landmarkList = fectchFavorites().toMutableList()
        landmarkList.add(landmark)
        val editor = sharedPreferences.edit()
        editor.putString(Constant.FAVORITES_PREF_KEY, Gson().toJson(landmarkList))
        editor.apply()
    }

    fun fectchFavorites():ArrayList<LandmarkModel>{
        var scoresJson = sharedPreferences?.getString(Constant.FAVORITES_PREF_KEY, null)
        if(scoresJson == null) {
            return arrayListOf<LandmarkModel>()
        }else{
            val landmarksType = object : TypeToken<MutableList<LandmarkModel>>() {}.type
            return Gson().fromJson(scoresJson, landmarksType)
        }
    }

    fun saveYelpToken(token: String){
        val editor = sharedPreferences.edit()
        val tokenStr = "Bearer $token"
        editor.putString(Constant.TOKEN_YELP_PREF, tokenStr)
        editor.apply()
    }

    fun fetchYelpToken(): String{
        val token = sharedPreferences?.getString(Constant.TOKEN_YELP_PREF,null)
        if (token == null){
            return ""
        }else{
            return token
        }
    }
}