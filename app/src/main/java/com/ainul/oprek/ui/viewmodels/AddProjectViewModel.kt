package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.databinding.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.Project
import com.ainul.oprek.repository.DatabaseRepository
import com.ainul.oprek.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddProjectViewModel constructor(private val app: Application) : ViewModel(), Observable {

    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val encryptManager = Util.EncryptManager(app)
    private val userId = encryptManager.getSession()!!.userId

    @get:Bindable
    var deviceName: String = ""

    @get:Bindable
    var description: String = ""

    @get:Bindable
    var customerName: String = ""

    @get:Bindable
    var phoneNumber: String = ""

    @get:Bindable
    var cost: String = ""

    @get:Bindable
    var dueDate: String = ""

    fun onClick() {
        val projectData = Project(
            userId = userId,
            deviceName = deviceName,
            customerName = customerName,
            description = null,
            phoneNumber = null,
            dueDate = null
        )

        uiScope.launch {
            repository.addProjectToDatabase(projectData)
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddProjectViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddProjectViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}