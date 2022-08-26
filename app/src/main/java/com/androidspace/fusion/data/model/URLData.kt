package com.androidspace.fusion.data.model

import com.google.gson.annotations.SerializedName

data class URLData(
    val id:Long = 0,
    @SerializedName("url")
    val url: String)