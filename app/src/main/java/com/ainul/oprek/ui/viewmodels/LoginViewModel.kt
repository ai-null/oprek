package com.ainul.oprek.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.BR
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(app: Application): ViewModel(), Observable {
    companion object {
        enum class AuthenticationState {
            AUTHENTICATED, UNAUTHENTICATED
        }
    }

    // defines database and repository
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // thread handler
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    @get:Bindable
    var email: String = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.email)
        }

    @get:Bindable
    var pin: String = ""
        set(value) {
            field = value
            callbacks.notifyChange(this, BR.pin)
        }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    // Error state holder
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun onClick() {
        if (email.isNotBlank() && pin.isNotBlank()) {
            login()
        } else {
            _error.value = "Email or pin is required"
        }
    }

    /**
     * login user to the app,
     * throws an error using `_error` state when email / pin is incorrect
     */
    private fun login() {
        uiScope.launch {
            if (repository.validateUser(email, pin.toInt())) saveSession()
            else _error.value = "email or pin is incorrect"
        }
    }

    private val _authenticationState = MutableLiveData(AuthenticationState.UNAUTHENTICATED)
    val authenticationState: LiveData<AuthenticationState> get() = _authenticationState

    /**
     * Save email & pin to the app,
     * so user won't need to login every time they open the app
     */
    private fun saveSession() {
        _authenticationState.value = AuthenticationState.AUTHENTICATED
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}