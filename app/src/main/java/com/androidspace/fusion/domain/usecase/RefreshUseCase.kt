package com.androidspace.fusion.domain.usecase

import com.androidspace.fusion.data.model.TokenResponseData
import com.androidspace.fusion.data.repository.Repository
import com.androidspace.fusion.domain.BaseUseCase


class RefreshUseCase(val repo: Repository): BaseUseCase<TokenResponseData, Unit>() {

    override suspend fun onLaunch(params: Unit): TokenResponseData? = repo.reftesh()
}