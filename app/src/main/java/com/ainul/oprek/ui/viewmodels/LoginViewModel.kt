package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.User
import com.ainul.oprek.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // thread manager
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun onClick() {

    }

    private fun login(data: User) {
        uiScope.launch {

        }
    }

    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}