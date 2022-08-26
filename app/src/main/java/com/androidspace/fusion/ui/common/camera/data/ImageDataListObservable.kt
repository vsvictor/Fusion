package com.androidspace.fusion.ui.common.camera.data

import android.text.TextUtils
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.androidspace.fusion.data.model.MediaData
import com.androidspace.fusion.data.model.camera.ImageData
import com.androidspace.fusion.BR
class ImageDataListObservable(private var _images: List<ImageData>): BaseObservable() {
    @get:Bindable
    var images get() = _images; set(value) {
        _images = value; notifyPropertyChanged(BR._all)
    }
    val last: Int get() {
        var res = -1
        for(item in images){
            if(!TextUtils.isEmpty(item.uri.toString())) res++
        }
        return res
    }
    fun put(num: Int, data: MediaData){
        if(num > images.size-1){

        }else {
            (images as ArrayList<MediaData>)[num] = data
            notifyPropertyChanged(BR._all)
        }
    }
}