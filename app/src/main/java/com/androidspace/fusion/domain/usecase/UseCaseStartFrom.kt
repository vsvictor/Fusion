package com.androidspace.fusion.domain.usecase

import android.text.TextUtils
import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase

class UseCaseStartFrom(val repo: Repository): MasterUseCase() {
    var mapType: Int get() = repo.getStartFrom();set(value) {repo.setStartFrom(value)}
}