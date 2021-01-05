package com.ainul.oprek.utils

import android.content.Context
import android.view.View
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.snackbar.Snackbar

class Util {
    companion object {
        @JvmStatic
        fun showSnackBar(view: View, message: String) {
            Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Encrypt manager,
     * this class used to retrieve data from localSession using `EncryptedSharedPref`.
     * since there will be a lot of screens using this, so i make it inside Util class
     *
     * @constructor context Context used for encrypt/decrypt
     */
    class EncryptManager(context: Context) {

        // this will be our private key for the app, also with the schema to encrypt
        private val masterKey = MasterKey.Builder(context, "oprek-master-key")
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // instance of SharedPref, use this to safe, retrieve, or delete session
        private val sharedPref = EncryptedSharedPreferences.create(
            context,
            "com.ainul.oprek.user",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        /**
         * Retrieve session from local, and decrypt the data
         * returns null when there's no session
         *
         * @return [CurrentSession]? nullable data class contains user data
         */
        fun getSession(): CurrentSession? {
            val id = sharedPref.getLong("userId", -1L)
            val email = sharedPref.getString("email", null)
            val pin = sharedPref.getString("pin", null)

            return if (email != null && pin != null) {
                CurrentSession(id, email, pin)
            } else {
                null
            }
        }

        /**
         * save user data to local
         *
         * @param email String
         * @param pin Int
         * @return Boolean returns false when there's an error occurring during process
         */
        fun saveSession(userId: Long, email: String, pin: String) {
            with(sharedPref.edit()) {
                putLong("userId", userId)
                putString("email", email)
                putString("pin", pin)
                apply()
            }
        }

        fun removeSession() {
            with(sharedPref.edit()) {
                remove("userId")
                remove("email")
                remove("pin")
                apply()
            }
        }

        data class CurrentSession(val userId: Long, val email: String, val pin: String)
    }
}