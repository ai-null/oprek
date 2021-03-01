package com.ainul.oprek.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.adapter.ListItemAdapter
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.databinding.ActivityMainBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel
import com.ainul.oprek.util.Constants


class MainActivity : AppCompatActivity(), ListItemListener {

    private lateinit var binding: ActivityMainBinding
    private val viewmodel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(this.application)
        ).get(MainViewModel::class.java)
    }

    // Adapter used for recyclerView
    private lateinit var adapter: ListItemAdapter

    // SearchView
    private lateinit var searchView: SearchView
    private lateinit var queryTextListener: SearchView.OnQueryTextListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        // setup action bar
        val toolbar: Toolbar = binding.mainToolbar
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        binding.viewmodel = viewmodel

        // set up adapter for recyclerView & attach the adapter to the View
        adapter = ListItemAdapter(this)
        binding.recentProjectsList.adapter = adapter

        // FloatingActionButton to add project clickListener
        binding.addProjectFab.setOnClickListener {
            goToAddProject()
        }

        // state-change watcher
        updateLiveData()
    }

    private fun goToAddProject() {
        val intent = Intent(this, AddProjectActivity::class.java)
        startActivity(intent)
    }

    private fun updateLiveData() {
        // called after state change from logout menu clicked
        viewmodel.logoutState.observe(this, {
            if (it) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        // this will automatically called (deleted/added) when there's change on the database
        // since the dao returns it as LiveData
        viewmodel.projects.observe(this, {
            it?.let { projects ->
                adapter.submitList(projects)
            }
        })

        viewmodel.filteredList.observe(this, {
            it.let { projects ->
                adapter.submitList(projects)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.item_search)
        val searchManager: SearchManager =
            this.getSystemService(Context.SEARCH_SERVICE) as (SearchManager)

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.componentName))
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()) {
                    val searchActivity = Intent(this@MainActivity, SearchActivity::class.java)

                    searchActivity.putExtra(Constants.QUERY, query)
                    startActivityForResult(searchActivity, Constants.REQUEST_CODE_SEARCH_ACTIVITY)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = true

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile -> {
                startActivityForResult(
                    Intent(this, ProfileActivity::class.java),
                    Constants.REQUEST_CODE_PROFILE_ACTIVITY
                )
            }

            // navigate to login screen. called logout method in the viewmodel, update state,
            // tell the state-change watcher to proceed to logout
            R.id.item_logout -> viewmodel.logout()
        }

        // assign the listener created before
        searchView.setOnQueryTextListener(queryTextListener)

        return super.onOptionsItemSelected(item)
    }

    override fun mainClickListener(project: Project) {
        val intent = Intent(this, DetailProjectActivity::class.java)
        intent.putExtra(Constants.PROJECT_ID, project.id)

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_CODE_PROFILE_ACTIVITY
            && resultCode == Constants.RESULT_CODE_UPDATED
        ) {
            viewmodel.refresh()
        }
    }
}