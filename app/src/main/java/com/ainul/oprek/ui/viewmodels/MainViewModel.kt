package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.Project
import com.ainul.oprek.util.Constants.Status
import com.ainul.oprek.util.Util
import java.lang.IllegalArgumentException

class MainViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)

    // defines encryptManager to get current userId
    private val encryptManager = Util.EncryptManager(app)
    private val userId = encryptManager.getSession()!!.userId

    // no need to make this into a method in repo, since it returns LiveData
    // LiveData will automatically handle the project from background-thread
    val projects: LiveData<List<Project>?> = database.oprekDao.getProjects(userId)

    fun projectCount(projectList: List<Project>?, status: Status): String {
        val statusFilter: Int = projectList?.filter {
            it.status == status.value
        }?.size ?: 0

        return statusFilter.toString()
    }

    // remove current session
    fun logout() {
        encryptManager.removeSession()
        _logoutState.value = true
    }

    private val _logoutState = MutableLiveData(false)
    val logoutState: LiveData<Boolean> get() = _logoutState

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("unable to construct viewmodel")
        }

    }
}