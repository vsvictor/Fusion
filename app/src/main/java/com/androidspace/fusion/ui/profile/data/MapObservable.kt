package com.androidspace.fusion.ui.profile.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.androidspace.fusion.BR
import com.androidspace.fusion.data.model.MapData

class MapObservable(private var _maps:List<MapData>): BaseObservable() {
    @get:Bindable
    var maps get() = _maps;set(value) {
        _maps = value; notifyPropertyChanged(BR._all)
    }
}