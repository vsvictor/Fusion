package com.androidspace.fusion.data.repository

import com.androidspace.fusion.ui.map.data.MapType


interface LocalDataSource{
    fun setFirstStart(isFirst: Boolean)
    fun isFirstStart():Boolean
    fun getAccessToken(): String
    fun setAccessToken(token: String)
    fun getRefreshToken(): String
    fun setRefreshToken(token: String)
    fun clearTokens()
    fun setUseFingerPrint(use: Boolean)
    fun isUseFingerPrint():Boolean
    fun setAgree(agree: Boolean)
    fun isAgree(): Boolean
    fun getMapType(): MapType
    fun setMapType(type: MapType)
    fun getStartFrom(): Int
    fun setStartFrom(startFrom: Int)
    fun getBasemap(): String
    fun setBasemap(basemapID: String)
}