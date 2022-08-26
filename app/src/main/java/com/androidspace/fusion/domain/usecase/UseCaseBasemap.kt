package com.androidspace.fusion.domain.usecase

import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase


class UseCaseBasemap(val repo: Repository): MasterUseCase() {
    var basemap:String get() = repo.getBasemap();set(value) {repo.setBasemap(value)}
}