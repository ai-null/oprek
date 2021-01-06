package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.databinding.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.BR
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.Project
import com.ainul.oprek.repository.DatabaseRepository
import com.ainul.oprek.utils.Constants
import com.ainul.oprek.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddProjectViewModel constructor(
    app: Application,
    private val projectId: Long? = null
) : ViewModel(), Observable {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // thread handler
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    @get:Bindable
    var deviceName: String = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.deviceName)
        }

    @Bindable
    var description: String? = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.description)
        }

    @get:Bindable
    var customerName: String = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.customerName)
        }

    @get:Bindable
    var phoneNumber: String? = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.phoneNumber)
        }

    @get:Bindable
    var cost: String = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.cost)
        }

    @get:Bindable
    var dueDate: String? = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.dueDate)
        }

    init {
        if (projectId != null) {
            uiScope.launch {
                val project = repository.getProject(projectId)!!

                deviceName = project.deviceName
                description = project.description
                customerName = project.customerName
                phoneNumber = project.phoneNumber
                cost = project.cost.toString()
                dueDate = project.dueDate
            }
        }
    }

    // defines encryptManager to get current userId
    private val encryptManager = Util.EncryptManager(app)
    private val userId = encryptManager.getSession()!!.userId

    // error state handler
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    fun onClick() {
        if (deviceName.isNotBlank() and customerName.isNotBlank()) {
            val costCheck = if (cost.isBlank()) 0.0 else cost.toDouble()

            saveProject(
                Project(
                    id = projectId ?: 0L,
                    userId = userId,
                    deviceName = deviceName,
                    customerName = customerName,
                    description = description,
                    phoneNumber = phoneNumber,
                    dueDate = dueDate,
                    cost = costCheck
                )
            )
        } else {
            _error.value = "Device name and Customer name can't be empty"
        }
    }

    // when its true, tell the UI to navigateBack to parent-activity
    private val _successAddProject = MutableLiveData<Number>()
    val successAddProject: LiveData<Number> get() = _successAddProject

    /**
     * save project to database using repository
     * the repository throws an error when deviceName and customerName is blank
     *
     * @param projectData [Project]
     */
    private fun saveProject(projectData: Project) {
        uiScope.launch {
            try {
                if (projectId == null) {
                    repository.addProjectToDatabase(projectData)
                    _successAddProject.value = Constants.PROJECT_ADDED
                } else {
                    repository.updateProjectToDatabase(projectData)
                    _successAddProject.value = Constants.PROJECT_UPDATED
                }
            } catch (e: NullPointerException) {
                _error.value = e.message // Error while saving the project
            }
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    class Factory(private val app: Application, private val projectId: Long? = null) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddProjectViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddProjectViewModel(app, projectId) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }
}