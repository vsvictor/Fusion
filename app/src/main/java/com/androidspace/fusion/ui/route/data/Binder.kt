package com.androidspace.fusion.ui.route.data

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator

class Binder {
    companion object{
        @JvmStatic
        @BindingAdapter("app:map_loader")
        fun bindLoader(progress: CircularProgressIndicator, state: Boolean){
            progress.isVisible = state
        }
    }
}