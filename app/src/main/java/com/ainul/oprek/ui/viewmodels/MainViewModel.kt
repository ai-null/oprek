package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.repository.DatabaseRepository
import com.ainul.oprek.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.lang.IllegalArgumentException

class MainViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val encryptManager = Util.EncryptManager(app)

    fun logout() {
        encryptManager.removeSession()
        _logoutState.value = true
    }

    private val _logoutState = MutableLiveData(false)
    val logoutState: LiveData<Boolean> get() = _logoutState

    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("unable to construct viewmodel")
        }

    }
}