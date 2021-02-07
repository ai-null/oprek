package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.repository.DatabaseRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class DetailViewModel constructor(app: Application, private val projectId: Long) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // thread handler
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    // project data
    private val _projectData = MutableLiveData<Project>()
    val projectData: LiveData<Project> get() = _projectData

    init {
        refresh() // init project data
    }

    fun refresh() {
        getProject()
    }

    /**
     * grep project data from database
     * this used for initial load or refresh data only after update
     */
    private fun getProject() {
        uiScope.launch {
            _projectData.value = repository.getProject(projectId)
        }
    }

    /**
     * Delete project
     *
     * this method called via menu-item, it delay 700ms before set [_navigateBack] to true,
     * so snackBar can be showed properly.
     * {@see [com.ainul.oprek.ui.activities.DetailProjectActivity]}
     */
    fun onDelete() {
        uiScope.launch {
            repository.deleteProject(projectId)
            delay(700L)
            _navigateBack.value = true
        }
    }

    fun updateStatus(status: Int) {
        uiScope.launch {
            repository.updateStatus(projectId, status)
            refresh() // refresh data after status updated
        }
    }

    // activity cycle UI state, when set to true will trigger `finish()` method
    private val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    class Factory(private val app: Application, private val userId: Long) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(app, userId) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}