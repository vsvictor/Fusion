package com.androidspace.fusion.data.repository

import com.androidspace.fusion.base.errors.ExtException
import com.androidspace.fusion.data.model.TokenResponseData


interface RemoteDataSource{
    @Throws(ExtException::class)
    suspend fun refresh(): TokenResponseData?
}