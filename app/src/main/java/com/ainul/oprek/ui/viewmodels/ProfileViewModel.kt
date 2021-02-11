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

class ProfileViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // defines encryptManager to get current userId
    private val encryptManager = EncryptManager(app)
    private val userSession = encryptManager.getSession()!!

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private fun getUser() {
        uiScope.launch {
            _user.value = repository.getUser(userSession.email, userSession.pin)
        }
    }

    init {
        getUser()
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
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(app) as T
            }
            throw IllegalArgumentException("unable to construct viewmodel")
        }

    }
}