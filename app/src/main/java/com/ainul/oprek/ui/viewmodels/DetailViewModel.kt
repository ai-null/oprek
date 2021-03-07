package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.entities.Project
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.repository.DatabaseRepository
import com.ainul.oprek.util.Constants
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class DetailViewModel constructor(
    app: Application,
    private val projectId: Long,
    private val user: User
) : ViewModel() {
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

    private val _updateIncome = MutableLiveData<Boolean>()
    val updateIncome: LiveData<Boolean> get() = _updateIncome

    fun updateStatus(status: Int) {
        uiScope.launch {
            val statusDone = Constants.Status.DONE.value
            val data = _projectData.value!!
            if (status == statusDone) {
                // If done is clicked, add income to the User
                val payload: Double = user.addIncome(data.cost)
                repository.updateIncome(user.id, payload)
                _updateIncome.value = true
            } else {
                // otherwise, when other were clicked when project was done then reduce income
                if (data.status == statusDone) {
                    val payload: Double = user.removeIncome(data.cost)
                    repository.updateIncome(user.id, payload)
                    _updateIncome.value = true
                }
            }

            repository.updateStatus(projectId, status)

            refresh() // refresh data after status updated
        }
    }

    // activity cycle UI state, when set to true will trigger `finish()` method
    private val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    class Factory(
        private val app: Application,
        private val projectId: Long,
        private val user: User
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(app, projectId, user) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}