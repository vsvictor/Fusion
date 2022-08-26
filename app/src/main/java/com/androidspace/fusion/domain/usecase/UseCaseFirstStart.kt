package com.androidspace.fusion.domain.usecase

import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase


class UseCaseFirstStart(val repo: Repository): MasterUseCase() {
    var firstStart:Boolean get() = repo.isFirtsStart();set(value) {repo.setFirstStart(value)}
}