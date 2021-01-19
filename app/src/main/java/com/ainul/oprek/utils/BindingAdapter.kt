@file:Suppress("DEPRECATION")
@file:SuppressLint("UseCompatLoadingForDrawables")

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
import com.bumptech.glide.Glide
import java.io.File


@BindingAdapter("imagePath")
fun ImageView.imagePath(e: String?) {
    if (e == null) {
        setImageDrawable(resources.getDrawable(R.drawable.ic_round_add_photo_alternate_24))
    } else {
        Glide.with(this.context).load(File(e)).override(480).into(this)
    }
}

// set background of LinearLayout from [status_tag.xml]
@BindingAdapter("statusBackground")
fun LinearLayout.statusBackground(status: Int) {
    val color: Int = when (status) {
        Status.DONE.value -> R.color.backgroundGreen
        Status.CANCEL.value -> R.color.backgroundRed
        else -> R.color.backgroundYellow
    }

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

    setTextColor(resources.getColor(color))
    setText(text)
}

// set drawable of ImageView from [status_tag.xml]
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