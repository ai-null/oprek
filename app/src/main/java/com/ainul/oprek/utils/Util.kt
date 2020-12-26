package com.ainul.oprek.utils

import android.view.View
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
}