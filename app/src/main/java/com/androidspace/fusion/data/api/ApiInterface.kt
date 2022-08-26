package com.androidspace.fusion.data.api

import com.androidspace.fusion.data.model.TokenResponseData
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @Headers("Accept:application/json;Cache-Control:no-cache")
    @POST("refresh-token")
    suspend fun refresh(): Response<TokenResponseData>
}