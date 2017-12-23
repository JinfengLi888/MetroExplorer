package ff.gwu.edu.metroexplorer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Jinfeng on 9/19/17.
 */

@Parcelize
data class LandmarkModel(val id: String, val title: String,
                         val imageURL: String, val address: ArrayList<String>,
                         val lat: String, val lon: String,
                         val phone: String):Parcelable