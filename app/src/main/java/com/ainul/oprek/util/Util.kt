package com.ainul.oprek.util

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat

object Util {
    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    /**
     * format Double data-type into a currency string
     * example: 20000.0 -> 2,000
     *
     * @param raw Boolean
     *  returns formatted string without currency-symbol like $, in this case "Rp."
     */
    @JvmStatic
    fun currencyFormat(amount: Double, raw: Boolean = false): String {
        val formatted = DecimalFormat("###,###,###").format(amount)

        return if (raw) formatted else "Rp. $formatted"
    }

    /**
     * Check app's permission. Returns true if its granted and false otherwise
     * -
     * with this method it will cut more default boilerplate
     * and can be run on both activity & fragment with the correct context
     */
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
}