package com.ainul.oprek.ui.activities

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.ainul.oprek.R
import com.ainul.oprek.adapter.HistoryAdapter
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.databinding.ActivityHistoryBinding
import com.ainul.oprek.util.Constants

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    private var userId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.lifecycleOwner = this

        // make sure this intent has userId passed from MainActivity
        userId = intent.extras!!.getLong(Constants.USER_ID)

        // setting up adapter for recyclerView
        adapter = HistoryAdapter()
        binding.listHistory.adapter = adapter
    }

    override fun onStart() {
        super.onStart()


        fetchData()?.observe(this, {
            adapter.data = it
        })
    }

    private fun fetchData(): LiveData<List<Project>>? {
        val database = OprekDatabase.getDatabase(this).oprekDao

        return userId?.let { database.filterProject(it, Constants.Status.DONE.value) }
    }
}