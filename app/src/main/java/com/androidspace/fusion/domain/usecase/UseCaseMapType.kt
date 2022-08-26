package com.androidspace.fusion.domain.usecase

import android.text.TextUtils
import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase
import com.androidspace.fusion.ui.map.data.MapType

class UseCaseMapType(val repo: Repository): MasterUseCase() {
    var mapType: MapType get() = repo.getMapType();set(value) {repo.setMapType(value)}
}