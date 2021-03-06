package com.ainul.oprek.ui.viewmodels

import android.app.Application
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.entities.User
import com.ainul.oprek.repository.DatabaseRepository
import kotlinx.coroutines.*
import java.lang.Exception

class RegisterViewModel(app: Application) : ViewModel(), Observable {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // job & uiScope for managing thread
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    // these mutable variables contains user data to register
    @get:Bindable
    var username: String = ""

    @get:Bindable
    var company: String = ""

    @get:Bindable
    var repeatedPin: String = ""

    @get:Bindable
    var email: String = ""

    @get:Bindable
    var pin: String = ""

    private val _profilePicture = MutableLiveData<String?>(null)
    val profilePicture: LiveData<String?> get() = _profilePicture

    fun updateProfilePicture(e: String?) {
        _profilePicture.value = e
    }

    fun onClick() {
        if (!isInputEmpty()) {
            registerAccount()
        }
    }

    // contains state to navigateBack, fragment should be navigateBack when its true
    private val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    // to prevent registerScreen to navigateBack when user open it again
    fun navigateComplete() {
        _navigateBack.value = false
    }


    // SuccessRegister state holder
    private val _successRegister = MutableLiveData(false)
    val successRegister: LiveData<Boolean> get() = _successRegister

    /**
     * register account
     * checking whether there's none to have the same email address throws error otherwise
     *
     * this will set _successRegister state to true if program successfully compiled without error
     *
     * @throws Exception
     */
    private fun registerAccount() {
        uiScope.launch {
            try {
                repository.registerUserToDatabase(
                    User(
                        username = username,
                        email = email,
                        pin = pin.toInt(),
                        company = company,
                        profilePicture = _profilePicture.value,
                        income = 0.0
                    )
                )

                // the throw callback always break the code and not run the rest.
                // set to success after register successfully compiled, otherwise throw error
                _successRegister.value = true

                // delayed screen navigateUp()
                delay(700L)
                _navigateBack.value = true
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * called on `onDestroy`, cancel all thread processes
     */
    fun onFinish() {
        job.cancel()
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /**
     * registration input checker,
     * throws and error on empty value and when pin doesn't match the repeated pin
     *
     * map all list into an set of array and do a loop to check through each input
     *
     * @return Boolean
     */
    private fun isInputEmpty(): Boolean {
        val inputs = mapOf(1 to username, 2 to email, 3 to pin)

        for (i in inputs) {
            val key = i.key
            val value = i.value

            if (value.isBlank()) {
                if (key != 3) _error.value = "Username and Email address can't be empty"
                else _error.value = "Pin can't be empty"

                // break loop
                break
            } else {
                if (key == 3 && value != repeatedPin) _error.value = "Pin doesn't match"
                else _error.value = null
            }
        }

        return _error.value != null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
}