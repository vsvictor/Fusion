package com.androidspace.fusion.data.api

import com.androidspace.fusion.data.model.TokenResponseData


interface ApiHelper {
    suspend fun refresh(): TokenResponseData?
}