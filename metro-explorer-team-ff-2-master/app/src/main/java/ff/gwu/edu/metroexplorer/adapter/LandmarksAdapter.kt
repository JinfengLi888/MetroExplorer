package ff.gwu.edu.metroexplorer.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.activity.LandmarkDetailActivity
import ff.gwu.edu.metroexplorer.model.LandmarkModel
import kotlinx.android.synthetic.main.landmarkcell.view.*

class LandmarksAdapter(var contxt:Context, val landmarks: ArrayList<LandmarkModel>):
        RecyclerView.Adapter<LandmarksAdapter.ViewHolder>(){

    override fun getItemCount(): Int {
        return landmarks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val itemView = LayoutInflater.from(contxt).inflate(R.layout.landmarkcell,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(landmarks[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(landmark: LandmarkModel){
            itemView.landmarktitle.text = landmark.title
            itemView.setOnClickListener{
                val intent = Intent(contxt, LandmarkDetailActivity::class.java)
                intent.putExtra(Constant.LANDMARKDETAIL_INTENT, landmark)
                contxt.startActivity(intent)
            }
        }
    }
}