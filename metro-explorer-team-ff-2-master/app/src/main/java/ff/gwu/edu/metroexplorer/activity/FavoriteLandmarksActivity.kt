package ff.gwu.edu.metroexplorer.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.adapter.LandmarksAdapter
import ff.gwu.edu.metroexplorer.adapter.PersistanceManager
import kotlinx.android.synthetic.main.activity_favorite_landmarks.*
import kotlinx.android.synthetic.main.activity_landmarks.*
import kotlinx.android.synthetic.main.activity_metro_stations.*

class FavoriteLandmarksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_landmarks)
        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayout.VERTICAL
        favoriteslist.setHasFixedSize(true)
        favoriteslist.layoutManager = manager
        val itemDecorator = DividerItemDecoration(favoriteslist.context, manager.orientation)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider))
        favoriteslist.addItemDecoration(itemDecorator)

        val persistanceManger = PersistanceManager(this)
        val landmarks = persistanceManger.fectchFavorites()
        favoriteslist.adapter = LandmarksAdapter(this,landmarks)

        // set navigation back button
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.Favorite)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
