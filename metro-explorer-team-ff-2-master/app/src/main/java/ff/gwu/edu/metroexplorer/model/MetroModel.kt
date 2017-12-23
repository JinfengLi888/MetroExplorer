package ff.gwu.edu.metroexplorer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MetroModel (val code: String, val name: String,
                       val lat: String, val lon: String): Parcelable