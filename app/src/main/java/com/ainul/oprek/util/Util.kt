package com.ainul.oprek.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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

        // Check app's permission, returns true if its granted and false otherwise
        fun isPermitted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Get selected image path
         * selected image from method above will passed here to get the image's path
         *
         * @param contentResolver [ContentResolver]
         * @param contentUri [Uri] get meta-data from selectedImage to extract the path
         * @return filePath [String]
         */
        fun getSelectedImagePath(contentResolver: ContentResolver, contentUri: Uri): String {
            val filePath: String
            val cursor: Cursor? = contentResolver.query(
                contentUri,
                null,
                null,
                null,
                null
            )

            if (cursor == null) {
                filePath = contentUri.path!!
            } else {
                cursor.moveToFirst()
                val index: Int = cursor.getColumnIndex("_data")
                filePath = cursor.getString(index)
                cursor.close()
            }

            return filePath
        }

        /**
         * createImageFile,
         * this method takes [Activity] as parameter to get external directory to store created image.
         * the file created come from taken picture or image selected from storage,
         * then store it on specific folder for the app
         *
         * @param activity
         */
        @SuppressLint("SimpleDateFormat")
        @Throws(IOException::class)
        fun createImageFile(activity: Activity): File {
            val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            return File.createTempFile(
                "JPEG_${timestamp}",
                ".jpg",
                storageDir
            )
        }
    }

    /**
     * Encrypt manager,
     * this class used to retrieve data from localSession using [EncryptedSharedPreferences].
     * since there will be a lot of screens using this, so i make it inside Util class
     *
     * @constructor context [Context] used for encrypt/decrypt
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