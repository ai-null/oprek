package com.ainul.oprek.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.adapter.ListItemAdapter
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.databinding.ActivitySearchBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel
import com.ainul.oprek.util.Constants

class SearchActivity : AppCompatActivity(), ListItemListener {

    // binding & viewmodel
    private lateinit var binding: ActivitySearchBinding
    private val viewmodel: MainViewModel by lazy {
        val application = requireNotNull(this).application
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }

    // searchBar
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener

    // searchQuery
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.lifecycleOwner = this

        if (intent!!.hasExtra(Constants.QUERY)) {
            query = intent.extras?.getString(Constants.QUERY)!!
            supportActionBar?.title = query
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val adapter = ListItemAdapter(this)
        binding.resultList.adapter = adapter

        // this will automatically called (deleted/added) when there's change on the database
        // since the dao returns it as LiveData
        viewmodel.projects.observe(this, {
            it?.let { projects ->
                adapter.submitList(projects)
            }
        })

        viewmodel.filteredList.observe(this, {
            it.let { projects ->
                Log.i("triggered", "$projects")
                adapter.submitList(projects)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.search_item)
        val searchManager: SearchManager =
            this.getSystemService(Context.SEARCH_SERVICE) as (SearchManager)

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    viewmodel.filterProjects(query)
                    Log.i("filter?", "$query")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true
        }

//        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean = true
//
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                finish()
//                return true
//            }
//        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    viewmodel.filterProjects(query)
                    Log.i("filter?", "$query")
                    return true
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = true

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun mainClickListener(project: Project) {
        val detailProjectIntent = Intent(this, DetailProjectActivity::class.java)
        detailProjectIntent.putExtra(Constants.PROJECT_ID, project.id)

        startActivity(detailProjectIntent)
    }
}