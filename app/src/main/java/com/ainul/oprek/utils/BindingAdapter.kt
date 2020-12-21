package com.ainul.oprek.utils

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import com.ainul.oprek.ui.viewmodels.RegisterViewModel

@BindingAdapter("onTextChanged")
fun EditText.onTextChanged(model: RegisterViewModel) {
    doOnTextChanged { text, _, _, _ ->
        model.onInputChanged(id, text.toString())
    }
}