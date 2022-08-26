package com.androidspace.fusion.ui.common.camera.data

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.androidspace.fusion.data.model.camera.ImageData
import com.androidspace.fusion.ui.common.camera.GalleryViewModel

class Binder {
    companion object{

        @JvmStatic
        @BindingAdapter("app:images")
        fun bindGalleryImages(rv: RecyclerView, images: List<ImageData>?){
            images?.let {
                val vm = ViewModelProvider(rv.context as AppCompatActivity).get(GalleryViewModel::class.java)
                val adapter = ImagesAdapter(it, vm.viewModelScope, isAdd = vm.isAdd)
                //adapter.onSelect = {vm.onImageLoad(it)}
                adapter.onView = {}
                adapter.onAdd = { vm.onAdd() }
                rv.adapter = adapter
            }
        }

    }
}