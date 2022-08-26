package com.androidspace.fusion.data.repository

import com.androidspace.fusion.data.api.ApiHelper
import com.androidspace.fusion.data.model.TokenResponseData


class RemoteDataSourceImpl(private val apiHelper: ApiHelper):RemoteDataSource {
    override suspend fun refresh(): TokenResponseData? = try{apiHelper.refresh()}catch (ex: Throwable){ throw ex}
}