package com.ainul.oprek.util

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.reflect.KProperty

class ContentViewDelegate<in R : AppCompatActivity, out T : ViewDataBinding>(@LayoutRes private val layoutRes: Int) {
    private var binding: T? = null

    operator fun getValue(activity: R, property: KProperty<*>): T {
        if (binding == null) {
            binding = DataBindingUtil.setContentView<T>(activity, layoutRes).apply {
                lifecycleOwner = activity
            }
        }
        return binding!!
    }
}

fun <R : AppCompatActivity, T : ViewDataBinding> contentView(@LayoutRes layoutRes: Int): ContentViewDelegate<R, T> =
    ContentViewDelegate(layoutRes)