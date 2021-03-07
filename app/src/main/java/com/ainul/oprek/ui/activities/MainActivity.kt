package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.adapter.ListItemAdapter
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.database.entities.User
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

    // User id
    private var userId : User? = null

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

        binding.header.buttonHistory.setOnClickListener {
            goToHistory()
        }

        // state-change watcher
        updateLiveData()
    }

    private fun goToAddProject() {
        val intent = Intent(this, AddProjectActivity::class.java)
        startActivity(intent)
    }

    /**
     * this method used to observe state-change by viewmodel,
     * all LiveData observer must be put here
     */
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

        viewmodel.user.observe(this, {
            userId = it
        })
    }

    private fun goToHistory() {
        userId?.let {
            val historyIntent = Intent(this, HistoryActivity::class.java)
            historyIntent.putExtra(Constants.USER_ID, it.id)

            startActivity(historyIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // navigate to profileActivity
            R.id.item_profile -> {
                startActivityForResult(
                    Intent(this, ProfileActivity::class.java),
                    Constants.REQUEST_CODE_PROFILE_ACTIVITY
                )
            }

            // navigate to searchActivity
            R.id.item_search -> {
                val searchActivity = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchActivity)
            }

            // navigate to login screen. called logout method in the viewmodel, update state,
            // tell the state-change watcher to proceed to logout
            R.id.item_logout -> viewmodel.logout()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * list item clickListener,
     * it will put an extra [Constants.PROJECT_ID] which contain projectId to navigate to DetailActivity
     */
    override fun mainClickListener(project: Project) {
        userId?.let {
            val intent = Intent(this, DetailProjectActivity::class.java)
            intent.putExtra(Constants.PROJECT_ID, project.id)
            intent.putExtra(Constants.USER, it)

            startActivityForResult(intent, Constants.REQUEST_CODE_DETAIL_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Constants.RESULT_CODE_UPDATED) {
            viewmodel.refresh()
        }
    }
}