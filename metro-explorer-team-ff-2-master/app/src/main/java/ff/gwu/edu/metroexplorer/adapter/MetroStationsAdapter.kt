package ff.gwu.edu.metroexplorer.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.activity.LandmarksActivity
import ff.gwu.edu.metroexplorer.model.MetroModel
import kotlinx.android.synthetic.main.metrocell.view.*
import kotlin.concurrent.timerTask

/**
 * Created by Jinfeng on 9/19/17.
 */
class MetroStationsAdapter (var context: Context, val metros: ArrayList<MetroModel>): RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>()  {
    override fun getItemCount(): Int {
        return metros.size
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val itemView = LayoutInflater.from(context).inflate(R.layout.metrocell,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(metros[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(metro: MetroModel){
            itemView.metroname.text = metro.name
            itemView.setOnClickListener{
                val intent = Intent(context, LandmarksActivity::class.java)
                intent.putExtra(Constant.LANDMARKDETAIL_LAT, metro.lat)
                intent.putExtra(Constant.LANDMARKDETAIL_LON, metro.lon)
                intent.putExtra(Constant.LANDMARKDETAIL_STATIONNAME, metro.name)
                context.startActivity(intent)
            }
        }
    }
}