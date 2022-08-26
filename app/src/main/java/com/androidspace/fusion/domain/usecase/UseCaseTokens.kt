package com.androidspace.fusion.domain.usecase

import android.text.TextUtils
import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.MasterUseCase

class UseCaseTokens(val repo: Repository): MasterUseCase() {
    var access: String get() = repo.getAccessToken();set(value) {repo.setAccessToken(value)}
    var refresh: String get() = repo.getRefreshToken();set(value) {repo.setRefreshToken(value)}
    val notFound get() = TextUtils.isEmpty(access) && TextUtils.isEmpty(refresh)
    fun clear(){repo.clearTokens()}
    val isLoggined get() = !TextUtils.isEmpty(access)
}