package com.androidspace.fusion.ui.profile.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.androidspace.fusion.data.model.ProfileData
import com.androidspace.fusion.BR

class ProfileObservable(private var _profile: ProfileData): BaseObservable() {
    @get:Bindable
    var profile get() = _profile; set(value) {
        _profile = value; notifyPropertyChanged(BR._all)
    }
    val fullName: String get() {
        return profile.lastName+" "+profile.firstName+" "+profile.middleName
    }
    val ph: String get() = profile.phone
    val em: String? get() = profile.email
}