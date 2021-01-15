package com.ainul.oprek.utils

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ainul.oprek.R
import com.ainul.oprek.utils.Constants.Status


// set background of LinearLayout from [status_tag.xml]
@BindingAdapter("statusBackground")
fun LinearLayout.statusBackground(status: Int) {
    val color: Int = when (status) {
        Status.DONE.value -> R.color.backgroundGreen
        Status.CANCEL.value -> R.color.backgroundRed
        else -> R.color.backgroundYellow
    }

    @Suppress("DEPRECATION")
    backgroundTintList = ColorStateList.valueOf(resources.getColor(color))
}

// set text & textColor of TextView from [status_tag.xml]
@BindingAdapter("statusText")
fun TextView.statusText(status: Int) {
    val color: Int
    val text: Int

    when (status) {
        Status.DONE.value -> {
            color = R.color.textGreen
            text = R.string.done
        }
        Status.CANCEL.value -> {
            color = R.color.textRed
            text = R.string.cancel
        }
        else -> {
            color = R.color.textYellow
            text = R.string.progress
        }
    }

    @Suppress("DEPRECATION")
    setTextColor(resources.getColor(color))
    setText(text)
}

// set drawable of ImageView from [status_tag.xml]
@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter("statusIcon")
fun ImageView.statusIcon(status: Int) {
    val icon: Drawable? = resources.getDrawable(
        when (status) {
            Status.DONE.value -> R.drawable.ic_done
            Status.CANCEL.value -> R.drawable.ic_cancel
            else -> R.drawable.ic_progress
        }
    )

    setImageDrawable(icon)
}