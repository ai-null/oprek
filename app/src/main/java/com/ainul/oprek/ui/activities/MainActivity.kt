package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel

        // set up adapter for recyclerView & attach the adapter to the View
        adapter = ListItemAdapter(this)
        binding.recentProjectsList.adapter = adapter

        // FloatingActionButton to add project clickListener
        binding.addProjectFab.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            startActivity(intent)
        }

        // state-change watcher
        updateLiveData()
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logout -> {
                // navigate to login screen. called logout method in the viewmodel, update state,
                // tell the state-change watcher to proceed to logout
                viewmodel.logout()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(project: Project) {
        val intent = Intent(this, DetailProjectActivity::class.java)
        intent.putExtra(Constants.PROJECT_ID, project.id)

        startActivity(intent)
    }
}