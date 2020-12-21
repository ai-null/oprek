package com.ainul.oprek.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ainul.oprek.R
import com.ainul.oprek.database.OprekDatabase
import com.ainul.oprek.database.User
import com.ainul.oprek.repository.DatabaseRepository
import kotlinx.coroutines.*
import java.lang.Exception

class RegisterViewModel(app: Application) : ViewModel() {
    private val database = OprekDatabase.getDatabase(app)
    private val repository = DatabaseRepository(database)

    // job & uiScope for managing thread
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    // these mutable variables contains user data to register
    private var username: String = ""
    private var email: String = ""
    private var pin: String = ""
    private var repeatedPin: String = ""

    /**
     * Called whenever input changed, and set the data according to its View.id
     * this method will be called in BindingAdapter
     *
     * @param viewId Int - View.id
     * @param text String? - EditText value
     */
    fun onInputChanged(viewId: Int, text: String?) {
        when (viewId) {
            R.id.input_username -> username = text!!
            R.id.input_email -> email = text!!
            R.id.input_pin -> pin = text!!
            R.id.input_pin_repeat -> repeatedPin = text!!
        }
    }

    fun onClick() {
        if (!isInputEmpty()) {
            registerAccount()
        }
    }

    private val _successRegister = MutableLiveData<Boolean>(false)
    val successRegister: LiveData<Boolean> get() = _successRegister

    private fun registerAccount() {
        uiScope.launch {
            try {
                repository.registerUserToDatabase(
                    User(
                        username = username,
                        email = email,
                        pin = pin.toInt()
                    )
                )
            } catch (e: Exception) {
                Log.i("error", "${e.message}")
                _error.value = e.message
            }
            _successRegister.value = true
        }
    }

    /**
     * called on onDestroy, cancel all thread process
     */
    fun onFinish() {
        job.cancel()
    }

    // this liveData only holds error and send it to the UI
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private fun isInputEmpty(): Boolean {
        return if (username.isEmpty() || email.isEmpty()) {
            _error.value = "username & email can't be empty"
            true
        } else {
            return if (pin.isEmpty()) {
                _error.value = "Pin can't be empty"
                true
            } else {
                return if (pin != repeatedPin) {
                    _error.value = "Pin doesn't match"
                    true
                } else {
                    _error.value = null
                    false
                }
            }
        }
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
}