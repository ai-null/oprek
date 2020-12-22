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

    init {
        uiScope.launch {
            Log.i("user_data", "${repository.getAllUsers()}")
        }
    }

    fun onClick() {
        if (!isInputEmpty()) {
            registerAccount()
        }
    }


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
                        pin = pin.toInt()
                    )
                )

                // the throw callback always break the code and not run the rest.
                // set to success after register successfully compiled, otherwise throw error
                _successRegister.value = true
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
        val inputs = mapOf(1 to username, 1 to email, 2 to pin)

        for (i in inputs) {
            val key = i.key
            val value = i.value
            if (value.isEmpty()) {
                when (key) {
                    1 -> _error.value = "Username and Email address can't be empty"
                    2 -> _error.value = "Pin can't be empty"
                }
                break
            } else {
                if (key == 2 && value != repeatedPin) _error.value = "Pin doesn't match"
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
}