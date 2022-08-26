package com.androidspace.fusion.ui.profile.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.BR

class RegionsObservable(private var _regions: List<RegionData>): BaseObservable() {
    @get:Bindable
    var regions get() = _regions; set(value) {
        _regions = value; notifyPropertyChanged(BR._all)
    }
    fun update(){
        notifyPropertyChanged(BR._all)
    }
}