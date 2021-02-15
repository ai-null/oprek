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
import com.ainul.oprek.util.Constants.Status
import com.ainul.oprek.util.EncryptManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // defines encryptManager to get current userId
    private val encryptManager = EncryptManager(app)
    private val userSession = encryptManager.getSession()!!

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun refresh() {
        uiScope.launch {
            _user.value = repository.getUser(userSession.email, userSession.pin)
        }
    }

    init {
        refresh()
    }

    companion object {
        enum class DataToUpdate() {
            USERNAME,
            COMPANY,
            PROFILE_PICTURE
        }
    }

    private val _dataUpdated = MutableLiveData<Boolean>()
    val dataUpdated: LiveData<Boolean> get() = _dataUpdated

    /**
     * update user data, [dataToUpdate] will decide where the newValue should be saved into
     * @param newValue new data which will be updated
     */
    fun updateData(dataToUpdate: DataToUpdate, newValue: String) {
        uiScope.launch {
            repository.run {
                when (dataToUpdate) {
                    DataToUpdate.USERNAME -> updateUsername(userSession.userId, newValue)
                    DataToUpdate.COMPANY -> updateCompany(userSession.userId, newValue)
                    DataToUpdate.PROFILE_PICTURE -> updateProfilePicture(userSession.userId, newValue)
                }
            }.also {
                refresh()
                _dataUpdated.value = true
            }
        }
    }

    // no need to make this into a method in repo, since it returns LiveData
    // LiveData will automatically handle the project from background-thread
    val projects: LiveData<List<Project>?> by lazy {
        database.oprekDao.getProjects(userSession.userId)
    }

    /**
     * projectCount,
     * count how many projects filtered by its status
     *
     * @param projectList list of [Project], it is nullable in case there's no project yet
     * @param status [Status]
     * @return String
     */
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