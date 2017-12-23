package ff.gwu.edu.metroexplorer.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import ff.gwu.edu.metroexplorer.R
import ff.gwu.edu.metroexplorer.adapter.MetroStationsAdapter
import ff.gwu.edu.metroexplorer.model.MetroModel
import ff.gwu.edu.metroexplorer.task.FetchMetroStationsAsyncTask
import kotlinx.android.synthetic.main.activity_metro_stations.*

class MetroStationsActivity : AppCompatActivity(), FetchMetroStationsAsyncTask.FetchMetroCompletionListener, MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {

    private lateinit var metroList:ArrayList<MetroModel>

    // search view listener
    override fun onSearchViewClosed() {
        metroslist.adapter = MetroStationsAdapter(this, metroList)
    }

    override fun onSearchViewShown() {
        println("on search view shown ----------")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.e("on query text change:",newText)
        if (newText!=null && !newText.isEmpty()){
            val searchList = ArrayList<MetroModel>()
            for (item:MetroModel in metroList){
                if (item.name.contains(newText)){
                    searchList.add(item)
                }
            }
            metroslist.adapter = MetroStationsAdapter(this, searchList)
        }
        return true
    }

    // metro listener
    override fun metroLoaded (metros: ArrayList<MetroModel>) {
        metroslist.adapter = MetroStationsAdapter(this, metros)
        metroList = metros
        progressbar.visibility = View.GONE
    }

    override fun metroNotLoaded () {
        //TODO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metro_stations)

        // do sth to get metro stations
        val manager = LinearLayoutManager(this)
        manager.orientation = LinearLayout.VERTICAL
        metroslist.setHasFixedSize(true)
        metroslist.layoutManager = manager
        val itemDecorator = DividerItemDecoration(metroslist.context, manager.orientation)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider))
        metroslist.addItemDecoration(itemDecorator)

        val fetchmetros = FetchMetroStationsAsyncTask(this)
        fetchmetros.fetchMetroCompletionListener = this
        fetchmetros.fetch()

        // set navigation back button
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.metrostations)
        search_view.setOnQueryTextListener(this)
        search_view.setOnSearchViewListener(this)
        // set search view
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val item = menu?.findItem(R.id.action_search)
        search_view.setMenuItem(item)
        return true
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
