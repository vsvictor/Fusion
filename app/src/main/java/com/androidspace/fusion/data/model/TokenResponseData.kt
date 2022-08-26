package com.androidspace.fusion.data.model

import com.google.gson.annotations.SerializedName

data class TokenResponseData(
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresUn: Long,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String)