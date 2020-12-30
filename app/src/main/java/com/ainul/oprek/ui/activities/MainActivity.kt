package com.ainul.oprek.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.adapter.ListItemAdapter
import com.ainul.oprek.adapter.listener.ListItemListener
import com.ainul.oprek.databinding.ActivityMainBinding
import com.ainul.oprek.ui.viewmodels.MainViewModel

private const val ADD_PROJECT_REQUEST_CODE = 1
private const val DETAIL_PROJECT_REQUEST_CODE = 2

class MainActivity : AppCompatActivity(), ListItemListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(this.application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        // set up adapter for recyclerView and dummy data
        val adapter = ListItemAdapter(this)
        val data = mutableListOf<String>()
        val tempData = listOf("halo1", "halo2", "halo3", "halo4", "halo5")
        tempData.forEach {
            data.add(it)
        }

        // assign adapter & data we created above
        binding.recentProjectsList.adapter = adapter
        adapter.data = data

        // FloatingActionButton to add project clickListener
        binding.addProjectFab.setOnClickListener {
            val intent = Intent(this, AddProjectActivity::class.java)
            startActivityForResult(intent, ADD_PROJECT_REQUEST_CODE)
        }

        updateLiveData()
    }

    private fun updateLiveData() {
        viewModel.logoutState.observe(this, {
            if (it) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_about -> {
                Toast.makeText(this, "under construction", Toast.LENGTH_SHORT).show()
            }
            R.id.item_logout -> {
                viewModel.logout()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        val intent = Intent(this, DetailProjectActivity::class.java)
        startActivityForResult(intent, DETAIL_PROJECT_REQUEST_CODE)
    }
}