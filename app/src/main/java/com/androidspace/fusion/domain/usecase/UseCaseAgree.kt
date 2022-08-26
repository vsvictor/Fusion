package com.androidspace.fusion.domain.usecase

import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase


class UseCaseAgree(val repo: Repository): MasterUseCase() {
    var isAgree:Boolean get() = repo.isAgree();set(value) {repo.setAgree(value)}
}